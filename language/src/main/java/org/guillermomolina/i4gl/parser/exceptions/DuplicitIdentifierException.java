package org.guillermomolina.i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when a declaration of an identifier
 * such that was declared previously in the same scope occurs.
 */
public class DuplicitIdentifierException extends Exception {

    private static final long serialVersionUID = 7257207106359576073L;
    String identifier;

    public DuplicitIdentifierException(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getMessage() {
        return "Duplicit identifier: " + identifier;
    }
}