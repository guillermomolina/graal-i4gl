package com.guillermomolina.i4gl.runtime.exceptions;

/**
 * Exception thrown when a user tries to read from a file that is not opened for reading.
 */
public class NotOpenedToReadException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 8704732515419706874L;

    public NotOpenedToReadException() {
        super("This file is not opened to read");
    }

}
