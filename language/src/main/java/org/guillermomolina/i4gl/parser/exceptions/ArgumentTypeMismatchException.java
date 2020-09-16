package org.guillermomolina.i4gl.parser.exceptions;

/**
 * Exception thrown during parsing phase when actual argument type does not match formal argument type.
 */

public class ArgumentTypeMismatchException extends LexicalException {
    private static final long serialVersionUID = 181363083242440832L;

    public ArgumentTypeMismatchException(int argumentNumber) {
        super("Argument number " + argumentNumber + " type mismatch");
    }

}
