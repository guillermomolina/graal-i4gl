package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Null;

public class ObjectType extends BaseType {

    public static final ObjectType SINGLETON = new ObjectType();

    private ObjectType() {
    }    

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.hasMembers(value);
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
        return "OBJECT";
    }
}
