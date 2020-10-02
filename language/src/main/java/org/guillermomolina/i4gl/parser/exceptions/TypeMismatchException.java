package org.guillermomolina.i4gl.parser.exceptions;

public class TypeMismatchException extends LexicalException {
    private static final long serialVersionUID = 8228596073240133854L;

    public TypeMismatchException(final String leftType, final String rightType) {
        super("Can not assign a \"" + rightType + "\" to a \"" + leftType + "\"");
    }
}
