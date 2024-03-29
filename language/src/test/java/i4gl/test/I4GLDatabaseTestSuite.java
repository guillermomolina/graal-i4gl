package i4gl.test;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(I4GLSimpleTestRunner.class)
@I4GLTestSuite({"tests/database"})
public class I4GLDatabaseTestSuite {

    public static void main(String[] args) throws Exception {
        I4GLSimpleTestRunner.runInMain(I4GLSimpleTestSuite.class, args);
    }

    /*
     * Our "mx unittest" command looks for methods that are annotated with @Test. By just defining
     * an empty method, this class gets included and the test suite is properly executed.
     */
    @Test
    public void unittest() {
    }
}
