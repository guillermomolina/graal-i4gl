package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLCharType;
import i4gl.runtime.types.compound.I4GLTextType;
import i4gl.runtime.types.compound.I4GLVarcharType;

/**
 * Type descriptor representing the short type.
 */
public class I4GLSmallIntType extends I4GLType {

    public static final I4GLSmallIntType SINGLETON = new I4GLSmallIntType();

    private I4GLSmallIntType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Short;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return Short.valueOf((short) 0);
    }

    @Override
    public boolean convertibleTo(final I4GLType type) {
        return type == I4GLIntType.SINGLETON || type == I4GLBigIntType.SINGLETON || type == I4GLFloatType.SINGLETON
                || type instanceof I4GLVarcharType || type instanceof I4GLCharType || type == I4GLTextType.SINGLETON;
    }

    @Override
    public String toString() {
        return "SMALLINT";
    }
}