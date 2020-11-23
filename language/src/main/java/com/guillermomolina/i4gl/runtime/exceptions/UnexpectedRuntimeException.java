package com.guillermomolina.i4gl.runtime.exceptions;

/**
 * Generic runtime exception that is thrown when something unexpected has happened. In a hundred percent correct
 * implementation this exception shall never be thrown even if it may appear in a throw statement somewhere.
 */
public class UnexpectedRuntimeException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -3762853929240198407L;

    public UnexpectedRuntimeException() {
        super("Unexpected exception occurred");
    }

}
