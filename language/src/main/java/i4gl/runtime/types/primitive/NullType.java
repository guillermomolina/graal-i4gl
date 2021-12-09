package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Null;

public class NullType extends BaseType {

    public static final NullType SINGLETON = new NullType();

    private NullType() {
    }    

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.isNull(value);
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return Null.SINGLETON;
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return true;
    }

    @Override
    public String toString() {
        return "NULL";
    }
}
