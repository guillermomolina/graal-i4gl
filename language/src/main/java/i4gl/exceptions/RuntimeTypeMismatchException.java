package i4gl.exceptions;

import i4gl.runtime.types.BaseType;

/**
 * Exception thrown during parsing phase when a method call with wrong number of arguments occurs.
 */
public class RuntimeTypeMismatchException extends I4GLRuntimeException {

    private static final long serialVersionUID = -340954982340239L;

    public RuntimeTypeMismatchException(final BaseType expectedType, final BaseType returneddType) {
        super("Expression should return " + expectedType + " but returns " + returneddType);
    }

}
