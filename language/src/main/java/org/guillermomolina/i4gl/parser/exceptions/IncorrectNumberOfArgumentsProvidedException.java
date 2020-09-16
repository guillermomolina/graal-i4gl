package org.guillermomolina.i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when a method call with wrong number of arguments occurs.
 */
public class IncorrectNumberOfArgumentsProvidedException extends LexicalException {

    /**
     *
     */
    private static final long serialVersionUID = -6531664182149975980L;

    public IncorrectNumberOfArgumentsProvidedException(int required, int given) {
        super("Incorrect number of parameters provided. Required " + required + ", given: " + given);
    }

}
