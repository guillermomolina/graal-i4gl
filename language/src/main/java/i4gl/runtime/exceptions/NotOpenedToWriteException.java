package i4gl.runtime.exceptions;

/**
 * Exception thrown when a user tries to write to a file that is not opened for writing.
 */
public class NotOpenedToWriteException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -4778813721249917301L;

    public NotOpenedToWriteException() {
        super("This file is not opened to write");
    }

}
