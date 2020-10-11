package org.guillermomolina.i4gl.test;


import static com.oracle.truffle.tck.DebuggerTester.getSourceImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.DebugException;
import com.oracle.truffle.api.debug.DebugScope;
import com.oracle.truffle.api.debug.DebugStackFrame;
import com.oracle.truffle.api.debug.DebugValue;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SourceElement;
import com.oracle.truffle.api.debug.StepConfig;
import com.oracle.truffle.api.debug.SuspendAnchor;
import com.oracle.truffle.api.debug.SuspendedCallback;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.tck.DebuggerTester;
import org.graalvm.polyglot.HostAccess;

public class I4GLDebugTest {

    private DebuggerTester tester;

    @Before
    public void before() {
        tester = new DebuggerTester();
    }

    @After
    public void dispose() {
        tester.close();
    }

    private void startEval(Source code) {
        tester.startEval(code);
    }

    private static Source i4glCode(String code) {
        return Source.create("i4gl", code);
    }

    private DebuggerSession startSession() {
        return tester.startSession();
    }

    private DebuggerSession startSession(SourceElement... sourceElements) {
        return tester.startSession(sourceElements);
    }

    private String expectDone() {
        return tester.expectDone();
    }

    private void expectSuspended(SuspendedCallback callback) {
        tester.expectSuspended(callback);
    }

    protected SuspendedEvent checkState(SuspendedEvent suspendedEvent, String name, final int expectedLineNumber, final boolean expectedIsBefore, final String expectedCode,
                    final String... expectedFrame) {
        final int actualLineNumber = suspendedEvent.getSourceSection().getStartLine();
        Assert.assertEquals(expectedLineNumber, actualLineNumber);
        final String actualCode = suspendedEvent.getSourceSection().getCharacters().toString();
        Assert.assertEquals(expectedCode, actualCode);
        final boolean actualIsBefore = (suspendedEvent.getSuspendAnchor() == SuspendAnchor.BEFORE);
        Assert.assertEquals(expectedIsBefore, actualIsBefore);

        checkStack(suspendedEvent.getTopStackFrame(), name, expectedFrame);
        return suspendedEvent;
    }

    protected void checkStack(DebugStackFrame frame, String name, String... expectedFrame) {
        assertEquals(name, frame.getName());
        checkDebugValues("variables", frame.getScope(), expectedFrame);
    }

    protected void checkArgs(DebugStackFrame frame, String... expectedArgs) {
        Iterable<DebugValue> arguments = null;
        DebugScope scope = frame.getScope();
        while (scope != null) {
            if (scope.isFunctionScope()) {
                arguments = scope.getArguments();
                break;
            }
            scope = scope.getParent();
        }
        checkDebugValues("arguments", arguments, expectedArgs);
    }

    private static void checkDebugValues(String msg, DebugScope scope, String... expected) {
        Map<String, DebugValue> valMap = new HashMap<>();
        DebugScope currentScope = scope;
        while (currentScope != null) {
            for (DebugValue value : currentScope.getDeclaredValues()) {
                valMap.put(value.getName(), value);
            }
            currentScope = currentScope.getParent();
        }
        checkDebugValues(msg, valMap, expected);
    }

    private static void checkDebugValues(String msg, Iterable<DebugValue> values, String... expected) {
        Map<String, DebugValue> valMap = new HashMap<>();
        for (DebugValue value : values) {
            valMap.put(value.getName(), value);
        }
        checkDebugValues(msg, valMap, expected);
    }

    private static void checkDebugValues(String msg, Map<String, DebugValue> valMap, String... expected) {
        String message = String.format("Frame %s expected %s got %s", msg, Arrays.toString(expected), valMap.toString());
        Assert.assertEquals(message, expected.length / 2, valMap.size());
        for (int i = 0; i < expected.length; i = i + 2) {
            String expectedIdentifier = expected[i];
            String expectedValue = expected[i + 1];
            DebugValue value = valMap.get(expectedIdentifier);
            Assert.assertNotNull(value);
            Assert.assertEquals(expectedValue, value.toDisplayString());
        }
    }

    @Test
    public void testDebugger() throws Throwable {
        /*
         * Test AlwaysHalt is working.
         */
        final Source factorial = i4glCode("DEFINE MAIN\n" +
                        "  RETURN fac(5)\n" +
                        "END MAIN\n" +
                        "FUNCTION fac(n)\n" +
                        "  DEFINE n AS INTEGER\n" +
                        "  IF (n <= 1) THEN\n" +
                        "    DEBUGGER\n" + // // break
                        "    RETURN 1\n" +
                        "  END IF\n" +
                        "  RETURN n * fac(n - 1)" +
                        "END FUNCTION\n");

        try (DebuggerSession session = startSession()) {
            startEval(factorial);

            // make javac happy and use the session
            session.getBreakpoints();

            expectSuspended((SuspendedEvent event) -> {
                checkState(event, "fac", 6, true, "debugger", "n", "1").prepareContinue();
            });

            expectDone();
        }
    }

    @Test
    public void testDebugValue() throws Throwable {
        final Source varsSource = i4glCode("function main() {\n" +
                        "  a = doNull();\n" +
                        "  b = 10 == 10;\n" +
                        "  c = 10;\n" +
                        "  d = \"str\";\n" +
                        "  e = new();\n" +
                        "  e.p1 = 1;\n" +
                        "  e.p2 = new();\n" +
                        "  e.p2.p21 = 21;\n" +
                        "  return;\n" +
                        "}\n" +
                        "function doNull() {}\n");

        try (DebuggerSession session = startSession()) {
            session.install(Breakpoint.newBuilder(getSourceImpl(varsSource)).lineIs(10).build());
            startEval(varsSource);

            expectSuspended((SuspendedEvent event) -> {
                DebugStackFrame frame = event.getTopStackFrame();

                DebugScope scope = frame.getScope();
                DebugValue a = scope.getDeclaredValue("a");
                assertFalse(a.isArray());
                assertNull(a.getArray());
                assertNull(a.getProperties());

                DebugValue b = scope.getDeclaredValue("b");
                assertFalse(b.isArray());
                assertNull(b.getArray());
                assertNull(b.getProperties());

                DebugValue c = scope.getDeclaredValue("c");
                assertFalse(c.isArray());
                assertEquals("10", c.toDisplayString());
                assertNull(c.getArray());
                assertNull(c.getProperties());

                DebugValue d = scope.getDeclaredValue("d");
                assertFalse(d.isArray());
                assertEquals("str", d.toDisplayString());
                assertNull(d.getArray());
                assertNull(d.getProperties());

                DebugValue e = scope.getDeclaredValue("e");
                assertFalse(e.isArray());
                assertNull(e.getArray());
                assertEquals(scope, e.getScope());
                Collection<DebugValue> propertyValues = e.getProperties();
                assertEquals(2, propertyValues.size());
                Iterator<DebugValue> propertiesIt = propertyValues.iterator();
                assertTrue(propertiesIt.hasNext());
                DebugValue p1 = propertiesIt.next();
                assertEquals("p1", p1.getName());
                assertEquals("1", p1.toDisplayString());
                assertNull(p1.getScope());
                assertTrue(propertiesIt.hasNext());
                DebugValue p2 = propertiesIt.next();
                assertEquals("p2", p2.getName());
                assertNull(p2.getScope());
                assertFalse(propertiesIt.hasNext());

                propertyValues = p2.getProperties();
                assertEquals(1, propertyValues.size());
                propertiesIt = propertyValues.iterator();
                assertTrue(propertiesIt.hasNext());
                DebugValue p21 = propertiesIt.next();
                assertEquals("p21", p21.getName());
                assertEquals("21", p21.toDisplayString());
                assertNull(p21.getScope());
                assertFalse(propertiesIt.hasNext());

                DebugValue ep1 = e.getProperty("p1");
                assertEquals("1", ep1.toDisplayString());
                ep1.set(p21);
                assertEquals("21", ep1.toDisplayString());
                assertNull(e.getProperty("NonExisting"));
            });

            expectDone();
        }
    }
}
