package i4gl.nodes.builtin;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.I4GLBigIntArray;
import i4gl.runtime.values.I4GLChar;
import i4gl.runtime.values.I4GLCharArray;
import i4gl.runtime.values.I4GLFloatArray;
import i4gl.runtime.values.I4GLIntArray;
import i4gl.runtime.values.I4GLNull;
import i4gl.runtime.values.I4GLSmallFloatArray;
import i4gl.runtime.values.I4GLSmallIntArray;
import i4gl.runtime.values.I4GLVarchar;

@NodeInfo(shortName = "length")
public abstract class I4GLLengthBuiltinNode extends I4GLBuiltinNode {

    @Specialization
    int length(String string) {
        return string.length();
    }

    @Specialization
    int lenth(I4GLChar charValue) {
        return charValue.getLength();
    }

    @Specialization
    int lenth(I4GLVarchar varchar) {
        return varchar.getSize();
    }

    // Array length not actually supported by i4gl

    @Specialization
    int length(I4GLCharArray array) {
        return array.getSize();
    }

    @Specialization
    int length(I4GLSmallIntArray array) {
        return array.getSize();
    }

    @Specialization
    int length(I4GLIntArray array) {
        return array.getSize();
    }

    @Specialization
    int length(I4GLBigIntArray array) {
        return array.getSize();
    }

    @Specialization
    int length(I4GLSmallFloatArray array) {
        return array.getSize();
    }

    @Specialization
    int length(I4GLFloatArray array) {
        return array.getSize();
    }

    @Specialization
    int length(I4GLNull string) {
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