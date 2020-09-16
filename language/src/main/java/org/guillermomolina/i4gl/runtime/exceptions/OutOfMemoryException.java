package org.guillermomolina.i4gl.runtime.exceptions;

/**
 * This exception is thrown when the heap memory is full and the interpreter needs to allocate space for another object.
 * {@link org.guillermomolina.i4gl.runtime.heap.I4GLHeap}
 */
public class OutOfMemoryException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1489818721998322823L;

    public OutOfMemoryException() {
        super("Out of memory.");
    }
}
