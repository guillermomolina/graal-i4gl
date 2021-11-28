package i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when a declaration of an identifier
 * such that was declared previously in the same scope occurs.
 */
public class DuplicitIdentifierException extends LexicalException {

    private static final long serialVersionUID = 7257207106359576073L;

    public DuplicitIdentifierException(String identifier) {
        super("Duplicit identifier: " + identifier);
    }
}