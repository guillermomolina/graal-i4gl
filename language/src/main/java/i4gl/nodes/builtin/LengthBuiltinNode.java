package i4gl.nodes.builtin;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.Array;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.Varchar;

@NodeInfo(shortName = "length")
public abstract class LengthBuiltinNode extends BuiltinNode {
    @Specialization
    int lengthChar1(char[] array) {
        return array.length;
    }

    @Specialization
    int lengthSmallInt(short[] array) {
        return array.length;
    }

    @Specialization
    int lengthInt(int[] array) {
        return array.length;
    }

    @Specialization
    int lengthBigInt(long[] array) {
        return array.length;
    }

    @Specialization
    int lengthSmallFloat(float[] array) {
        return array.length;
    }

    @Specialization
    int lengthFloat(double[] array) {
        return array.length;
    }

    @Specialization
    int lengthObject(Object[] array) {
        return array.length;
    }

    @Specialization
    int length(final String string) {
        return string.length();
    }

    @Specialization
    int lenth(final Char charValue) {
        return charValue.getLength();
    }

    @Specialization
    int lenth(final Varchar varchar) {
        return varchar.getSize();
    }

    // Array length not actually supported by i4gl

    @Specialization
    int length(final Array array) {
        return array.getSize();
    }

    @Specialization
    int length(final Null string) {
        return 0;
    }

    @Specialization
    int length(final Object string) {
        throw new NotImplementedException("I4GLLengthBuiltinNode.length()");
    }

    @Override
    public BaseType getReturnType() {
        return IntType.SINGLETON;
    }
}