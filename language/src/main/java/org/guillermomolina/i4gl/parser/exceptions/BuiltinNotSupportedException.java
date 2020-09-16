package org.guillermomolina.i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when user uses a built-in subroutine that is not supported in Trupple.
 */
public class BuiltinNotSupportedException extends LexicalException {

    /**
     *
     */
    private static final long serialVersionUID = 2785716297448745948L;

    public BuiltinNotSupportedException() {
        super("This builtin is not supported");
    }

}
