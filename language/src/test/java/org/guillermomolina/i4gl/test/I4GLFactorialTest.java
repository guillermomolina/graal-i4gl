package org.guillermomolina.i4gl.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class I4GLFactorialTest {

    private Context context;
    private Value factorial;

    @Before
    public void initEngine() throws Exception {
        context = Context.create();
        // @formatter:off
        context.eval("i4gl",
            "FUNCTION factorial(n)\n" +
                "DEFINE n INT\n" +
                "IF n <= 1 THEN\n" +
                    "RETURN 1\n" +
                "END IF\n" +
                "RETURN n * factorial(n -1)\n" +
            "END FUNCTION\n"
        );
        // @formatter:on
        factorial = context.getBindings("i4gl").getMember("factorial");
    }

    @After
    public void dispose() {
        context.close();
    }

    @Test
    public void factorialOf5() throws Exception {
        Number ret = factorial.execute(5).as(Number.class);
        assertEquals(120, ret.intValue());
    }

    @Test
    public void factorialOf3() throws Exception {
        Number ret = factorial.execute(3).as(Number.class);
        assertEquals(6, ret.intValue());
    }

    @Test
    public void factorialOf1() throws Exception {
        Number ret = factorial.execute(1).as(Number.class);
        assertEquals(1, ret.intValue());
    }
}
