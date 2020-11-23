package com.guillermomolina.i4gl.runtime.exceptions;

/**
 * Exception thrown when a user provides invalid argument for the logarithm function ({@link com.guillermomolina.i4gl.nodes.builtin.arithmetic.LnBuiltinNode}).
 */
public class LogarithmInvalidArgumentException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 4191158269770192461L;

    public LogarithmInvalidArgumentException(double value) {
        super("Invalid value for logarithm function: " + value + ". The value must be greater than 0.");
    }
}
