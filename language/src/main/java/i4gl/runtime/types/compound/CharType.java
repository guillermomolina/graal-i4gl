package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.values.Char;

/**
 * Type descriptor representing the string type.
 */
public class CharType extends TextType {
    private final int size;

    public CharType(int size) {
        assert (size > 1 || (getClass() == Char1Type.class));
        this.size = size;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Char;
    }

    @Override
    public Object getDefaultValue() {
        return new Char(size);
    }

    @Override
    public String toString() {
        return "CHAR(" + size + ")";
    }
}