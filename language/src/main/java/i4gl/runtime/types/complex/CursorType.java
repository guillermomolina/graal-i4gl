package i4gl.runtime.types.complex;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Cursor;

/**
 * Specialized type descriptor for text-file values.
 */
public class CursorType extends BaseType {

    public static final CursorType SINGLETON = new CursorType();

    private CursorType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Cursor;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        throw new NotImplementedException();
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "CURSOR";
    }
}
