package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.values.I4GLChar;

/**
 * Type descriptor representing the string type.
 */
public class I4GLCharType extends I4GLTextType {
    private final int size;

    public I4GLCharType(int size) {
        this.size = size;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLChar;
    }

    @Override
    public Object getDefaultValue() {
        return new I4GLChar(size);
    }

    @Override
    public String toString() {
        return "CHAR(" + size + ")";
    }
}