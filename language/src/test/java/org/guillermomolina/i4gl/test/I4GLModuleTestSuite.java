package org.guillermomolina.i4gl.test;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(I4GLModuleTestRunner.class)
@I4GLTestSuite({"tests/module"})
public class I4GLModuleTestSuite {

    public static void main(String[] args) throws Exception {
        I4GLModuleTestRunner.runInMain(I4GLModuleTestSuite.class, args);
    }

    /*
     * Our "mx unittest" command looks for methods that are annotated with @Test. By just defining
     * an empty method, this class gets included and the test suite is properly executed.
     */
    @Test
    public void unittest() {
    }
}
