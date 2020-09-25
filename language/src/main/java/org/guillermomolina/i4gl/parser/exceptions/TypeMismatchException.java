package org.guillermomolina.i4gl.parser.exceptions;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

public class TypeMismatchException extends Exception {
    private static final long serialVersionUID = 8228596073240133854L;
    private TypeDescriptor leftType;
    private TypeDescriptor rightType;


    public TypeMismatchException(final TypeDescriptor leftType, final TypeDescriptor rightType) {
        this.leftType = leftType;
        this.rightType = rightType;
    }

    @Override
    public String getMessage() {
        return "Type " + leftType.toString() + " and " + rightType.toString() + " are not compatible";
    }
}
