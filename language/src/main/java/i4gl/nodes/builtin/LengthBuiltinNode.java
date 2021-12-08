package i4gl.nodes.builtin;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.BigIntArray;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.CharArray;
import i4gl.runtime.values.FloatArray;
import i4gl.runtime.values.IntArray;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.SmallFloatArray;
import i4gl.runtime.values.SmallIntArray;
import i4gl.runtime.values.Varchar;

@NodeInfo(shortName = "length")
public abstract class LengthBuiltinNode extends BuiltinNode {

    @Specialization
    int length(String string) {
        return string.length();
    }

    @Specialization
    int lenth(Char charValue) {
        return charValue.getLength();
    }

    @Specialization
    int lenth(Varchar varchar) {
        return varchar.getSize();
    }

    // Array length not actually supported by i4gl

    @Specialization
    int length(CharArray array) {
        return array.getSize();
    }

    @Specialization
    int length(SmallIntArray array) {
        return array.getSize();
    }

    @Specialization
    int length(IntArray array) {
        return array.getSize();
    }

    @Specialization
    int length(BigIntArray array) {
        return array.getSize();
    }

    @Specialization
    int length(SmallFloatArray array) {
        return array.getSize();
    }

    @Specialization
    int length(FloatArray array) {
        return array.getSize();
    }

    @Specialization
    int length(Null string) {
        return 0;
    }

    @Specialization
    int length(Object string) {
        throw new NotImplementedException("I4GLLengthBuiltinNode.length()");
    }

    @Override
    public BaseType getType() {
        return IntType.SINGLETON;
    }
}