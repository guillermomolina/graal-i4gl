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
 * Type descriptor representing the float type.
 * Uses java Double
 */
public class FloatType extends BaseType {

    public static final FloatType SINGLETON = new FloatType();

    private FloatType() {
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
    public Class<?> getPrimitiveClass() {
        return double.class;
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
        return "FLOAT";
    }

    @Override
    public String getNullString() {
        return " ".repeat(14);
    }

}