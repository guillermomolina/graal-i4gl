package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.DecimalType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.compound.VarcharType;

/**
 * Type descriptor representing the real type.
 */
public class SmallFloatType extends BaseType {

    public static final SmallFloatType SINGLETON = new SmallFloatType();

    private SmallFloatType() {
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
    public Class<?> getPrimitiveClass() {
        return float.class;
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return type == SmallIntType.SINGLETON || type == IntType.SINGLETON || type == BigIntType.SINGLETON
                || type == SmallFloatType.SINGLETON || type == FloatType.SINGLETON || type instanceof DecimalType
                || type instanceof VarcharType || type instanceof CharType || type == TextType.SINGLETON
                || type == DateType.SINGLETON;
    }

    @Override
    public String toString() {
        return "SMALLFLOAT";
    }

    @Override
    public String getNullString() {
        return " ".repeat(14);
    }
}