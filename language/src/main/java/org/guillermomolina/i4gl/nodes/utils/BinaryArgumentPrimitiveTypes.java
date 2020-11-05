package org.guillermomolina.i4gl.nodes.utils;

import org.guillermomolina.i4gl.runtime.types.I4GLType;

/**
 * Stores one set of types of arguments of binary operations. This utility class is used for static type checking. We
 * store allowed combinations of types of arguments into a table in each binary node and during the parsing the given
 * values are matched against this table.
 * {@link org.guillermomolina.i4gl.nodes.expression.I4GLBinaryExpressionNode}
 */
public class BinaryArgumentPrimitiveTypes {

    private final Tuple<I4GLType, I4GLType> types;

    public BinaryArgumentPrimitiveTypes(I4GLType leftType, I4GLType rightType) {
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

    public I4GLType getLeftType() {
        return this.types.getFirst();
    }

    public I4GLType getRightType() {
        return this.types.getSecond();
    }

}
