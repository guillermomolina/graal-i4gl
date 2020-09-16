package org.guillermomolina.i4gl.parser.exceptions;

/**
 * A generic exception for lexical errors. They are thrown during parsing phase, caught in the {@link org.guillermomolina.i4gl.parser.NodeFactory}
 * and their message is printed to the error output stream.
 */
public class LexicalException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -5362208927595320271L;
    private String message;

    public LexicalException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}