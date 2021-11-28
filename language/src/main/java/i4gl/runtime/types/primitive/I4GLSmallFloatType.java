package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLCharType;
import i4gl.runtime.types.compound.I4GLTextType;
import i4gl.runtime.types.compound.I4GLVarcharType;

/**
 * Type descriptor representing the real type.
 */
public class I4GLSmallFloatType extends I4GLType {

    public static final I4GLSmallFloatType SINGLETON = new I4GLSmallFloatType();

    private I4GLSmallFloatType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Float;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Float;
    }

    @Override
    public Object getDefaultValue() {
        return Float.valueOf(0.0f);
    }

    @Override
    public boolean convertibleTo(final I4GLType type) {
        return type instanceof I4GLVarcharType
        || type instanceof I4GLCharType || type == I4GLTextType.SINGLETON;
    }

    @Override 
    public String toString() {
        return "SMALLFLOAT";
    }
    
}