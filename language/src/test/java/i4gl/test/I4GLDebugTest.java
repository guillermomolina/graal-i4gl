package i4gl.test;

import static com.oracle.truffle.tck.DebuggerTester.getSourceImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.DebugScope;
import com.oracle.truffle.api.debug.DebugStackFrame;
import com.oracle.truffle.api.debug.DebugValue;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SuspendAnchor;
import com.oracle.truffle.api.debug.SuspendedCallback;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.tck.DebuggerTester;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        tester.startExecute(new Function<Context, Value>() {
            public Value apply(Context c) {
                c.eval(code);
                return c.getBindings("i4gl").getMember("MAIN").execute();
            }
        });
    }

    private static Source i4glCode(String code) {
        return Source.create("i4gl", code);
    }

    private DebuggerSession startSession() {
        return tester.startSession();
    }

    private String expectDone() {
        return tester.expectDone();
    }

    private void expectSuspended(SuspendedCallback callback) {
        tester.expectSuspended(callback);
    }

    protected SuspendedEvent checkState(SuspendedEvent suspendedEvent, String name, final int expectedLineNumber,
            final boolean expectedIsBefore, final String expectedCode, final String... expectedFrame) {
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

    private static void checkDebugValues(String msg, Map<String, DebugValue> valMap, String... expected) {
        String message = String.format("Frame %s expected %s got %s", msg, Arrays.toString(expected),
                valMap.toString());
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
        // @formatter:off
        final Source factorial = i4glCode(
            "MAIN\n" +
            "  CALL fac(5)\n" +
            "END MAIN\n" +
            "FUNCTION fac(n)\n" +
            "  DEFINE n INT\n" +
            "  IF (n <= 1) THEN\n" +
            "    BREAKPOINT\n" + // // break
            "    RETURN 1\n" +
            "  END IF\n" +
            "  RETURN n * fac(n - 1)\n" +
            "END FUNCTION\n");
        // @formatter:on
        try (DebuggerSession session = startSession()) {
            startEval(factorial);

            // make javac happy and use the session
            session.getBreakpoints();

            expectSuspended((SuspendedEvent event) -> {
                checkState(event, "fac", 7, true, "BREAKPOINT", "n", "INT 1").prepareContinue();
            });

            expectDone();
        }
    }

    @Test
    public void testDebugValue() throws Throwable {
        // @formatter:off
        final Source varsSource = i4glCode(
            "MAIN\n" +
            "  DEFINE a, b INT\n" +
            "  DEFINE c SMALLFLOAT\n" +
            "  DEFINE d TEXT\n" +
            "  DEFINE e RECORD\n" +
            "    p1 INT,\n" +
            "    p2 RECORD\n" +
            "      p21 INT\n" +
            "    END RECORD\n" +
            "  END RECORD\n" +
            "  LET a = doNull()\n" +
            "  LET b = 1\n" +
            "  LET c = 1.32\n" +
            "  LET d = \"str\"\n" +
            "  LET e.p1 = 1\n" +
            "  LET e.p2.p21 = 21\n" +
            "  DISPLAY \"MAIN\"\n" +
            "END MAIN\n" +
            "FUNCTION doNull()\n" +
            "END FUNCTION\n");
        // @formatter:on
        try (DebuggerSession session = startSession()) {
            session.install(Breakpoint.newBuilder(getSourceImpl(varsSource)).lineIs(17).build());
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
                assertEquals("INT 1", b.toDisplayString());
                assertNull(b.getArray());
                assertNull(b.getProperties());

                DebugValue c = scope.getDeclaredValue("c");
                assertFalse(c.isArray());
                assertEquals("SMALLFLOAT 1.32", c.toDisplayString());
                assertNull(c.getArray());
                assertNull(c.getProperties());

                DebugValue d = scope.getDeclaredValue("d");
                assertFalse(d.isArray());
                assertEquals("TEXT \"str\"", d.toDisplayString());
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
                assertEquals("INT 1", p1.toDisplayString());
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
                assertEquals("INT 21", p21.toDisplayString());
                assertNull(p21.getScope());
                assertFalse(propertiesIt.hasNext());

                DebugValue ep1 = e.getProperty("p1");
                assertEquals("INT 1", ep1.toDisplayString());
                ep1.set(p21);
                assertEquals("INT 21", ep1.toDisplayString());
                assertNull(e.getProperty("NonExisting"));
            });

            expectDone();
        }
    }
}
