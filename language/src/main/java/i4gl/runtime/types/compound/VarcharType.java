package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.values.Varchar;

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

}