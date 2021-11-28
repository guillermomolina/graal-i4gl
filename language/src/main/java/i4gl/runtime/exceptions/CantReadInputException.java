package i4gl.runtime.exceptions;

import java.io.IOException;

/**
 * Exception thrown when we are unable to read from the input.
 */
public class CantReadInputException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 5205476879057496085L;

    public CantReadInputException(IOException ioException) {
        super("Can't read from input: " + ioException.getMessage());
    }
}