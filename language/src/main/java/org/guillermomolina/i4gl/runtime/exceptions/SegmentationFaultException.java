package org.guillermomolina.i4gl.runtime.exceptions;

/**
 * This exception is thrown when the interpreter I4GL source tries to access invalid address in the heap.
 * {@link org.guillermomolina.i4gl.runtime.heap.I4GLHeap}
 */
public class SegmentationFaultException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 7210299263480574197L;

    public SegmentationFaultException() {
        super("Segmentation fault.");
    }
}
