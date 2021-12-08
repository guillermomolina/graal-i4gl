package i4gl.nodes.utils;

import i4gl.runtime.types.BaseType;

/**
 * Stores one set of types of arguments of binary operations. This utility class is used for static type checking. We
 * store allowed combinations of types of arguments into a table in each binary node and during the parsing the given
 * values are matched against this table.
 * {@link i4gl.nodes.expression.BinaryExpressionNode}
 */
public class BinaryArgumentPrimitiveTypes {

    private final Tuple<BaseType, BaseType> types;

    public BinaryArgumentPrimitiveTypes(BaseType leftType, BaseType rightType) {
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

    public BaseType getLeftType() {
        return this.types.getFirst();
    }

    public BaseType getRightType() {
        return this.types.getSecond();
    }

}
