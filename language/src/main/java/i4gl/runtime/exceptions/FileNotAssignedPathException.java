package i4gl.runtime.exceptions;

/**
 * Exception thrown when user tries to do I/O operation on a file variable but it has not assigned a path to a file.
 */
public class FileNotAssignedPathException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -137178671723097185L;

    public FileNotAssignedPathException() {
        super("This file has no assigned path, yet");
    }

}
