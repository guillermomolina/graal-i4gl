package i4gl.runtime.context;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Null;

final class FunctionType extends BaseType {

    public static final FunctionType SINGLETON = new FunctionType();

    FunctionType() {
    }    

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.isExecutable(value);
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
        return false;
    }

    @Override
    public String toString() {
        return "FUNCTION";
    }
}
