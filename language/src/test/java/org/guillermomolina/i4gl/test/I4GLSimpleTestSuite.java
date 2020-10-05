package org.guillermomolina.i4gl.test;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(I4GLTestRunner.class)
@I4GLTestSuite({"tests"})
public class I4GLSimpleTestSuite {

    public static void main(String[] args) throws Exception {
        I4GLTestRunner.runInMain(I4GLSimpleTestSuite.class, args);
    }

    /*
     * Our "mx unittest" command looks for methods that are annotated with @Test. By just defining
     * an empty method, this class gets included and the test suite is properly executed.
     */
    @Test
    public void unittest() {
    }
}
