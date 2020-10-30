/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.guillermomolina.i4gl.launcher;

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
