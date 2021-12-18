package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Varchar;

/**
 * Type descriptor representing the string type.
 */
public class VarcharType extends ArrayType {

    public VarcharType(int size) {
        super(size, Char1Type.SINGLETON);
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Varchar;
    }

    @Override
    public Object getDefaultValue() {
        return new Varchar(size);
    }

    @Override
    public String toString() {
        return "VARCHAR(" + size + ")";
    }

    @Override
    public String getNullString() {
        return "";
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return type == TextType.SINGLETON;
    }
}