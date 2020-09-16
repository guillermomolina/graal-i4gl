package org.guillermomolina.i4gl.nodes.utils;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Stores one set of types of arguments of binary operations. This utility class is used for static type checking. We
 * store allowed combinations of types of arguments into a table in each binary node and during the parsing the given
 * values are matched against this table.
 * {@link org.guillermomolina.i4gl.nodes.BinaryExpressionNode}
 */
public class BinaryArgumentPrimitiveTypes {

    private final Tuple<TypeDescriptor, TypeDescriptor> types;

    public BinaryArgumentPrimitiveTypes(TypeDescriptor leftType, TypeDescriptor rightType) {
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

    public TypeDescriptor getLeftType() {
        return this.types.getFirst();
    }

    public TypeDescriptor getRightType() {
        return this.types.getSecond();
    }

}
