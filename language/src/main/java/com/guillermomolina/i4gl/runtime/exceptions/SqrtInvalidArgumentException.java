package com.guillermomolina.i4gl.runtime.exceptions;

/**
 * Exception thrown when a user provides invalid argument for the square root function ({@link com.guillermomolina.i4gl.nodes.builtin.arithmetic.SqrtBuiltinNode}).
 */
public class SqrtInvalidArgumentException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8691373953985740580L;

    public SqrtInvalidArgumentException(double value) {
        super("Invalid value for square root function: " + value + ". The value must be greater than 0.");
    }
}
