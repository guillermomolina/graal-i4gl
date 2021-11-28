package i4gl.runtime.exceptions;

/**
 * Exception thrown during parsing phase when a method call with wrong number of arguments occurs.
 */
public class IncorrectNumberOfArgumentsProvidedException extends I4GLRuntimeException {

    private static final long serialVersionUID = -6531664182149975980L;

    public IncorrectNumberOfArgumentsProvidedException(int required, int given) {
        super("Incorrect number of parameters provided. Required " + required + ", given: " + given);
    }

}
