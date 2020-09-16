package org.guillermomolina.i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when negation of a value that can not be negated occurs in I4GL source.
 */
public class CantBeNegatedException extends LexicalException {

    /**
     *
     */
    private static final long serialVersionUID = 4386515211072198351L;

    public CantBeNegatedException() {
        super("This type cannot be negated");
    }
}
