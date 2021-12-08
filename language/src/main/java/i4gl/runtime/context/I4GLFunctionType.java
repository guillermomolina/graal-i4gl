package i4gl.runtime.context;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.I4GLNull;

final class I4GLFunctionType extends BaseType {

    public static final I4GLFunctionType SINGLETON = new I4GLFunctionType();

    I4GLFunctionType() {
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
        return I4GLNull.SINGLETON;
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
