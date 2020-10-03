package org.guillermomolina.i4gl.nodes.utils;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

/**
 * Stores one set of types of arguments of binary operations. This utility class is used for static type checking. We
 * store allowed combinations of types of arguments into a table in each binary node and during the parsing the given
 * values are matched against this table.
 * {@link org.guillermomolina.i4gl.nodes.I4GLBinaryExpressionNode}
 */
public class BinaryArgumentPrimitiveTypes {

    private final Tuple<I4GLTypeDescriptor, I4GLTypeDescriptor> types;

    public BinaryArgumentPrimitiveTypes(I4GLTypeDescriptor leftType, I4GLTypeDescriptor rightType) {
        this.types = new Tuple<>(leftType, rightType);
    }

    @Override
    public boolean equals(Object compareTo) {
        if (!(compareTo instanceof BinaryArgumentPrimitiveTypes)) {
            return false;
        }

        BinaryArgumentPrimitiveTypes compareToArgs = (BinaryArgumentPrimitiveTypes) compareTo;
        return compareToArgs.types.getFirst().equals(this.types.getFirst()) && compareToArgs.types.getSecond().equals(this.types.getSecond());
    }

    @Override
    public int hashCode() {
        return types.getFirst().hashCode() * types.getSecond().hashCode();
    }

    public I4GLTypeDescriptor getLeftType() {
        return this.types.getFirst();
    }

    public I4GLTypeDescriptor getRightType() {
        return this.types.getSecond();
    }

}
