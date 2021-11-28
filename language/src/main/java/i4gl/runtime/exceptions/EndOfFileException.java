package i4gl.runtime.exceptions;

/**
 * Exception is thrown when the user tries to read from a file but he has already reached its end.
 */
public class EndOfFileException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6618509735533715833L;

    public EndOfFileException() {
        super("Reached the end of file");
    }

}
