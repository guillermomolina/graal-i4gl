package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.values.I4GLVarchar;

/**
 * Type descriptor representing the string type.
 */
public class VarcharType extends TextType {
    private final int size;

    public VarcharType(int size) {
        this.size = size;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLVarchar;
    }

    @Override
    public Object getDefaultValue() {
        return new I4GLVarchar(size);
    }

    @Override
    public String toString() {
        return "VARCHAR(" + size + ")";
    }

}