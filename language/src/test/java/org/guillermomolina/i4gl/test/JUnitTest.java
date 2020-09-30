package org.guillermomolina.i4gl.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public abstract class JUnitTest {

    protected ByteArrayOutputStream output;
    private Context context;

    @Before
    public void setUp() {
        output = new ByteArrayOutputStream();
        context = Context.newBuilder(I4GLLanguage.ID).allowExperimentalOptions(true).in(System.in).out(System.out)
                .err(System.err).build();
        // assertTrue(engine.getLanguages().containsKey(I4GLLanguage.MIME_TYPE));
        System.setOut(new PrintStream(output));
    }

    private void clearOutput() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    protected void test(String sourceCode, String expectedOutput) {
        test(sourceCode, new ArrayList<>(), expectedOutput);
    }

    protected void test(String sourceCode, List<String> imports, String expectedOutput) {
        test(sourceCode, imports, expectedOutput, new String[0]);
    }

    protected void test(String sourceCode, List<String> imports, String expectedOutput, Object[] arguments) {
        clearOutput();

        for (String importSource : imports) {
            context.eval(createSource(importSource));
        }
        context.eval(createSource(sourceCode)).execute(arguments);
        assertEquals(expectedOutput, output.toString());
    }

    Source createSource(String source) {
        try {
            return Source.newBuilder(I4GLLanguage.ID, source, "<testCode>").build();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    void cleanupFile(String filePath) {
        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Could not cleanup test file: " + filePath);
        }
    }

}
