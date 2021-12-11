package i4gl.runtime.types.complex;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.I4GLRuntimeException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Database;

/**
 * Specialized type descriptor for text-file values.
 */
public class DatabaseType extends BaseType {

    private final String alias;

    public DatabaseType(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Database;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new Database(alias);
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "DATABASE(" + alias + ")";
    }

    @Override
    public String getNullString() {
        throw new I4GLRuntimeException("Should not be here");
    }
}
