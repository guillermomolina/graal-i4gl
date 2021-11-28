package i4gl.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import com.oracle.truffle.api.dsl.NodeFactory;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import i4gl.I4GLLanguage;
import i4gl.nodes.builtin.I4GLBuiltinNode;
import i4gl.test.I4GLModuleTestRunner.TestCase;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.internal.TextListener;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public class I4GLModuleTestRunner extends ParentRunner<TestCase> {
    private static final String SOURCE_SUFFIX = ".4gl";
    private static final String INPUT_FILE_NAME = "input";
    private static final String OUTPUT_FILE_NAME = "output";

    private static final String LF = System.getProperty("line.separator");

    static class TestCase {
        protected final Description name;
        protected final Map<String, Path> sources;
        protected final String testInput;
        protected final String expectedOutput;
        protected final Map<String, String> options;
        protected String actualOutput;

        protected TestCase(Class<?> testClass, String baseName, Map<String, Path> sources, String testInput,
                String expectedOutput, Map<String, String> options) {
            this.name = Description.createTestDescription(testClass, baseName);
            this.sources = sources;
            this.testInput = testInput;
            this.expectedOutput = expectedOutput;
            this.options = options;
        }
    }

    private final List<TestCase> testCases;

    public I4GLModuleTestRunner(Class<?> runningClass) throws InitializationError {
        super(runningClass);
        try {
            testCases = createTests(runningClass);
        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    @Override
    protected Description describeChild(TestCase child) {
        return child.name;
    }

    @Override
    protected List<TestCase> getChildren() {
        return testCases;
    }

    protected static List<TestCase> createTests(final Class<?> c) throws IOException, InitializationError {
        I4GLTestSuite suite = c.getAnnotation(I4GLTestSuite.class);
        if (suite == null) {
            throw new InitializationError(String.format("@%s annotation required on class '%s' to run with '%s'.",
                    I4GLTestSuite.class.getSimpleName(), c.getName(), I4GLModuleTestRunner.class.getSimpleName()));
        }

        String[] paths = suite.value();
        Map<String, String> options = new HashMap<>();
        String[] optionsList = suite.options();
        for (int i = 0; i < optionsList.length; i += 2) {
            options.put(optionsList[i], optionsList[i + 1]);
        }

        Class<?> testCaseDirectory = c;
        if (suite.testCaseDirectory() != I4GLTestSuite.class) {
            testCaseDirectory = suite.testCaseDirectory();
        }
        Path root = getRootViaResourceURL(testCaseDirectory, paths);

        if (root == null) {
            for (String path : paths) {
                Path candidate = FileSystems.getDefault().getPath(path);
                if (Files.exists(candidate)) {
                    root = candidate;
                    break;
                }
            }
        }
        if (root == null && paths.length > 0) {
            throw new FileNotFoundException(paths[0]);
        }

        final Path rootPath = root;

        final List<TestCase> foundCases = new ArrayList<>();

        Files.walk(rootPath, 1).filter(entry -> !entry.equals(rootPath)).filter(Files::isDirectory)
                .forEach(subdirectory -> {
                    try {
                        foundCases.add(createTest(c, subdirectory, options));
                    } catch (IOException e) {
                        // Do Nothing
                    }
                });
        return foundCases;
    }

    private static TestCase createTest(final Class<?> c, Path testPath, final Map<String, String> options)
            throws IOException {
        String baseName = testPath.getFileName().toString();
        Map<String, Path> sources = new HashMap<String, Path>();

        try (Stream<Path> paths = Files.walk(testPath, 1)) {
            paths.filter(Files::isRegularFile).filter(f -> f.getFileName().toString().endsWith(SOURCE_SUFFIX))
                    .forEach(sourceFile -> {
                        sources.put(sourceFile.getFileName().toString(), sourceFile);
                    });
        }

        Path inputFile = testPath.resolve(INPUT_FILE_NAME);
        String testInput = "";
        if (Files.exists(inputFile)) {
            testInput = readAllLines(inputFile);
        }

        Path outputFile = testPath.resolve(OUTPUT_FILE_NAME);
        String expectedOutput = "";
        if (Files.exists(outputFile)) {
            expectedOutput = readAllLines(outputFile);
        }

        return new TestCase(c, baseName, sources, testInput, expectedOutput, options);
    }

    /**
     * Recursively deletes a file that may represent a directory.
     */
    private static void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            PrintStream err = System.err;
            err.println("Failed to delete file: " + f);
        }
    }

    /**
     * Unpacks a jar file to a temporary directory that will be removed when the VM
     * exits.
     *
     * @param jarfilePath the path of the jar to unpack
     * @return the path of the temporary directory
     */
    private static String explodeJarToTempDir(File jarfilePath) {
        try {
            final Path jarfileDir = Files.createTempDirectory(jarfilePath.getName());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    delete(jarfileDir.toFile());
                }
            });
            jarfileDir.toFile().deleteOnExit();
            JarFile jarfile = new JarFile(jarfilePath);
            Enumeration<JarEntry> entries = jarfile.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                if (!e.isDirectory()) {
                    File path = new File(jarfileDir.toFile(), e.getName().replace('/', File.separatorChar));
                    File dir = path.getParentFile();
                    dir.mkdirs();
                    assert dir.exists();
                    Files.copy(jarfile.getInputStream(e), path.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            final String path = jarfileDir.toFile().getAbsolutePath();
            jarfile.close();
            return path;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static Path getRootViaResourceURL(final Class<?> c, String[] paths) {
        URL url = c.getResource(c.getSimpleName() + ".class");
        if (url != null) {
            char sep = File.separatorChar;
            String externalForm = url.toExternalForm();
            String classPart = sep + c.getName().replace('.', sep) + ".class";
            String prefix = null;
            String base;
            if (externalForm.startsWith("jar:file:")) {
                prefix = "jar:file:";
                int bang = externalForm.indexOf('!', prefix.length());
                Assume.assumeTrue(bang != -1);
                File jarfilePath = new File(externalForm.substring(prefix.length(), bang));
                Assume.assumeTrue(jarfilePath.exists());
                base = explodeJarToTempDir(jarfilePath);
            } else if (externalForm.startsWith("file:")) {
                prefix = "file:";
                base = externalForm.substring(prefix.length(), externalForm.length() - classPart.length());
            } else {
                return null;
            }
            for (String path : paths) {
                String candidate = base + sep + path;
                if (new File(candidate).exists()) {
                    return FileSystems.getDefault().getPath(candidate);
                }
            }
        }
        return null;
    }

    private static String readAllLines(Path file) throws IOException {
        // fix line feeds for non unix os
        StringBuilder outFile = new StringBuilder();
        for (String line : Files.readAllLines(file, Charset.defaultCharset())) {
            outFile.append(line).append(LF);
        }
        return outFile.toString();
    }

    private static final List<NodeFactory<? extends I4GLBuiltinNode>> builtins = new ArrayList<>();

    public static void installBuiltin(NodeFactory<? extends I4GLBuiltinNode> builtin) {
        builtins.add(builtin);
    }

    @Override
    protected void runChild(TestCase testCase, RunNotifier notifier) {
        notifier.fireTestStarted(testCase.name);

        Context context = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (NodeFactory<? extends I4GLBuiltinNode> builtin : builtins) {
                I4GLLanguage.installBuiltin(builtin);
            }

            Context.Builder builder = Context.newBuilder().allowExperimentalOptions(true)
                    .in(new ByteArrayInputStream(testCase.testInput.getBytes("UTF-8"))).out(out);
            for (Map.Entry<String, String> e : testCase.options.entrySet()) {
                builder.option(e.getKey(), e.getValue());
            }
            context = builder.build();
            PrintWriter printer = new PrintWriter(out);
            run(context, testCase.sources.values(), printer);
            printer.flush();

            String actualOutput = new String(out.toByteArray());
            Assert.assertEquals(testCase.name.toString(), testCase.expectedOutput, actualOutput);
        } catch (Throwable ex) {
            notifier.fireTestFailure(new Failure(testCase.name, ex));
        } finally {
            if (context != null) {
                context.close();
            }
            notifier.fireTestFinished(testCase.name);
        }
    }

    private static void run(Context context, Collection<Path> paths, PrintWriter out) throws IOException {
        try {
            for (Path path : paths) {
                /* Parse the I4GL source file. */
                Source source = Source.newBuilder(I4GLLanguage.ID, path.toFile()).interactive(true).build();

                /* Call the main entry point, without any arguments. */
                context.eval(source);
            }
            final Value mainFunction = context.getBindings(I4GLLanguage.ID).getMember("MAIN");
            if (mainFunction != null) {
                mainFunction.execute();
            }
        } catch (PolyglotException ex) {
            if (!ex.isInternalError()) {
                out.println(ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    public static void runInMain(Class<?> testClass, String[] args) throws InitializationError, NoTestsRemainException {
        JUnitCore core = new JUnitCore();
        core.addListener(new TextListener(System.out));
        I4GLModuleTestRunner suite = new I4GLModuleTestRunner(testClass);
        if (args.length > 0) {
            suite.filter(new NameFilter(args[0]));
        }
        Result r = core.run(suite);
        if (!r.wasSuccessful()) {
            System.exit(1);
        }
    }

    private static final class NameFilter extends Filter {
        private final String pattern;

        private NameFilter(String pattern) {
            this.pattern = pattern.toLowerCase();
        }

        @Override
        public boolean shouldRun(Description description) {
            return description.getMethodName().toLowerCase().contains(pattern);
        }

        @Override
        public String describe() {
            return "Filter contains " + pattern;
        }
    }
}
