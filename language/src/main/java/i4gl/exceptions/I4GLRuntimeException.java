package i4gl.exceptions;

/**
 * A generic runtime exception with arbitrary message.
 */
public class I4GLRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 7001598203022655633L;
	
	public I4GLRuntimeException(String message) {
		super(message);
	}

}
