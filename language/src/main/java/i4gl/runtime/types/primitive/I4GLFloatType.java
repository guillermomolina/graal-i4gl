package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLCharType;
import i4gl.runtime.types.compound.I4GLTextType;
import i4gl.runtime.types.compound.I4GLVarcharType;

/**
 * Type descriptor representing the float type.
 * Uses java Double
 */
public class I4GLFloatType extends I4GLType {

    public static final I4GLFloatType SINGLETON = new I4GLFloatType();

    private I4GLFloatType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Double;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Double;
    }

    @Override
    public Object getDefaultValue() {
        return Double.valueOf(0.0d);
    }

    @Override
    public boolean convertibleTo(final I4GLType type) {
        return type instanceof I4GLVarcharType || type instanceof I4GLCharType
                || type == I4GLTextType.SINGLETON;
    }

    @Override
    public String toString() {
        return "FLOAT";
    }
}