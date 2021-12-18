package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Char;

/**
 * Type descriptor representing the string type.
 */
public class CharType extends ArrayType {

    public CharType(int size) {
        super(size, Char1Type.SINGLETON);
        assert (size > 1 || (getClass() == Char1Type.class));
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

    @Override
    public String getNullString() {
        return " ".repeat(size);
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return type == TextType.SINGLETON;
    }
}