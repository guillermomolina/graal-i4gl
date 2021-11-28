package i4gl.runtime.exceptions;

/**
 * Exception thrown during parsing phase when a method call with wrong number of arguments occurs.
 */
public class IncorrectNumberOfReturnValuesException extends I4GLRuntimeException {

    private static final long serialVersionUID = -2824872761372346923L;

    public IncorrectNumberOfReturnValuesException(int required, int returned) {
        super("Incorrect number of return values. Required " + required + ", returned: " + returned);
    }

}
