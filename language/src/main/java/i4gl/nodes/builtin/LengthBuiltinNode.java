package i4gl.nodes.builtin;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.Array;
import i4gl.runtime.values.Char;
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
    int length(Array array) {
        return array.getSize();
    }

    @Specialization
    int length(Object string) {
        throw new NotImplementedException("I4GLLengthBuiltinNode.length()");
    }

    @Override
    public BaseType getReturnType() {
        return IntType.SINGLETON;
    }
}