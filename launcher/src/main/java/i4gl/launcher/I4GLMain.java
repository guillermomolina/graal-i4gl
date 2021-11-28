package i4gl.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public final class I4GLMain {

    private static final String LANGUAGE_ID = "i4gl";

    /**
     * The main entry point.
     */
    public static void main(String[] args) throws IOException {
        Map<String, String> options = new HashMap<>();
        List<String> files = new ArrayList<>();
        for (String arg : args) {
            if (!parseOption(options, arg)) {
                files.add(arg);
            }
        }

        try {
            execute(options, files);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static int execute(final Map<String, String> options, final List<String> files) {

        try (Context context = Context.newBuilder(LANGUAGE_ID).allowExperimentalOptions(true).in(System.in)
                .out(System.out).options(options).build()) {
            /* 
             * Do not remove this yet
             * Engine engine = context.getEngine(); System.out.println("== Running on " +
             * engine.getImplementationName() + " " + engine.getVersion() + " ==");
             */

            if (files.isEmpty()) {
                Source source = Source.newBuilder(LANGUAGE_ID, new InputStreamReader(System.in), "<stdin>").build();
                context.eval(source);
            } else {
                for (String fileName : files) {
                    Source source = Source.newBuilder(LANGUAGE_ID, new File(fileName)).build();
                    context.eval(source);
                }
            }

            final Value mainFunction = context.getBindings(LANGUAGE_ID).getMember("MAIN");
            if (mainFunction == null) {
                System.err.println("No MAIN defined in the 4gl sources.");
                return -1;
            }
            mainFunction.execute();
            return 0;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return -1;
        } catch (PolyglotException ex) {
            if (ex.isInternalError()) {
                // for internal errors we print the full stack trace
                ex.printStackTrace();
            } else {
                System.err.println(ex.getMessage());
            }
            return -1;
        }
    }

    private static boolean parseOption(Map<String, String> options, String arg) {
        if (arg.length() <= 2 || !arg.startsWith("--")) {
            return false;
        }
        int eqIdx = arg.indexOf('=');
        String key;
        String value;
        if (eqIdx < 0) {
            key = arg.substring(2);
            value = null;
        } else {
            key = arg.substring(2, eqIdx);
            value = arg.substring(eqIdx + 1);
        }

        if (value == null) {
            value = "true";
        }
        /*int index = key.indexOf('.');
        String group = key;
        if (index >= 0) {
            group = group.substring(0, index);
        }*/
        options.put(key, value);
        return true;
    }

}
