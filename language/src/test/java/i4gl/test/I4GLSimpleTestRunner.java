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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.oracle.truffle.api.dsl.NodeFactory;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
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

import i4gl.I4GLLanguage;
import i4gl.nodes.builtin.BuiltinNode;
import i4gl.test.I4GLSimpleTestRunner.TestCase;

public class I4GLSimpleTestRunner extends ParentRunner<TestCase> {

    private static final String SOURCE_SUFFIX = ".4gl";
    private static final String INPUT_SUFFIX = ".input";
    private static final String OUTPUT_SUFFIX = ".output";

    private static final String LF = System.getProperty("line.separator");

    static class TestCase {
        protected final Description name;
        protected final Path path;
        protected final String sourceName;
        protected final String testInput;
        protected final String expectedOutput;
        protected final Map<String, String> options;
        protected String actualOutput;

        protected TestCase(Class<?> testClass, String baseName, String sourceName, Path path, String testInput, String expectedOutput, Map<String, String> options) {
            this.name = Description.createTestDescription(testClass, baseName);
            this.sourceName = sourceName;
            this.path = path;
            this.testInput = testInput;
            this.expectedOutput = expectedOutput;
            this.options = options;
        }
    }

    private final List<TestCase> testCases;

    public I4GLSimpleTestRunner(Class<?> runningClass) throws InitializationError {
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
            throw new InitializationError(String.format("@%s annotation required on class '%s' to run with '%s'.", I4GLTestSuite.class.getSimpleName(), c.getName(), I4GLSimpleTestRunner.class.getSimpleName()));
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
        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path sourceFile, BasicFileAttributes attrs) throws IOException {
                String sourceName = sourceFile.getFileName().toString();
                if (sourceName.endsWith(SOURCE_SUFFIX)) {
                    String baseName = sourceName.substring(0, sourceName.length() - SOURCE_SUFFIX.length());

                    Path inputFile = sourceFile.resolveSibling(baseName + INPUT_SUFFIX);
                    String testInput = "";
                    if (Files.exists(inputFile)) {
                        testInput = readAllLines(inputFile);
                    }

                    Path outputFile = sourceFile.resolveSibling(baseName + OUTPUT_SUFFIX);
                    String expectedOutput = "";
                    if (Files.exists(outputFile)) {
                        expectedOutput = readAllLines(outputFile);
                    }

                    foundCases.add(new TestCase(c, baseName, sourceName, sourceFile, testInput, expectedOutput, options));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return foundCases;
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
     * Unpacks a jar file to a temporary directory that will be removed when the VM exits.
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
            jarfile.close();
            return jarfileDir.toFile().getAbsolutePath();
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

    private static final List<NodeFactory<? extends BuiltinNode>> builtins = new ArrayList<>();

    public static void installBuiltin(NodeFactory<? extends BuiltinNode> builtin) {
        builtins.add(builtin);
    }

    @Override
    protected void runChild(TestCase testCase, RunNotifier notifier) {
        notifier.fireTestStarted(testCase.name);

        Context context = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (NodeFactory<? extends BuiltinNode> builtin : builtins) {
                I4GLLanguage.installBuiltin(builtin);
            }

            Context.Builder builder = Context.newBuilder().allowExperimentalOptions(true).allowHostClassLookup((s) -> true).allowHostAccess(HostAccess.ALL).in(
                            new ByteArrayInputStream(testCase.testInput.getBytes("UTF-8"))).out(out);
            for (Map.Entry<String, String> e : testCase.options.entrySet()) {
                builder.option(e.getKey(), e.getValue());
            }
            context = builder.build();
            PrintWriter printer = new PrintWriter(out);
            run(context, testCase.path, printer);
            printer.flush();

            testCase.actualOutput = new String(out.toByteArray());
            Assert.assertEquals(testCase.name.toString(), testCase.expectedOutput.length(), testCase.actualOutput.length());

            String[] actualOutputSplitted = testCase.actualOutput.split(LF);
            String[] expectedOutputSplitted = testCase.expectedOutput.split(LF);
            Assert.assertEquals(testCase.name.toString(), actualOutputSplitted.length, expectedOutputSplitted.length);
            for (int i = 0; i < actualOutputSplitted.length; i++) {
                Assert.assertEquals(testCase.name.toString() + " line: " + (i + 1), expectedOutputSplitted[i], actualOutputSplitted[i]);
            };
            Assert.assertEquals(testCase.name.toString(), testCase.expectedOutput, testCase.actualOutput);
        } catch (Throwable ex) {
            notifier.fireTestFailure(new Failure(testCase.name, ex));
        } finally {
            if (context != null) {
                context.close();
            }
            notifier.fireTestFinished(testCase.name);
        }
    }

    private static void run(Context context, Path path, PrintWriter out) throws IOException {
        try {
            /* Parse the I4GL source file. */
            Source source = Source.newBuilder(I4GLLanguage.ID, path.toFile()).interactive(true).build();

            /* Call the main entry point, without any arguments. */
            context.eval(source);
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
        I4GLSimpleTestRunner suite = new I4GLSimpleTestRunner(testClass);
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
