package org.guillermomolina.i4gl.runtime.exceptions;

/**
 * Exception thrown when user is accessing an array at non-existing index,
 */
public class IndexOutOfBoundsException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -2867470753887611077L;

    public IndexOutOfBoundsException() {
        super("Index out of bounds");
    }

}
