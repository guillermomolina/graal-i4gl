package org.guillermomolina.i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when usage of an undefined identifier occurs.
 */
public class UnknownIdentifierException extends Exception{
    private static final long serialVersionUID = 8004726989589411166L;
    private final String identifier;

    public UnknownIdentifierException(final String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getMessage() {
        return "Unknown identifier " + identifier;
    }
}
