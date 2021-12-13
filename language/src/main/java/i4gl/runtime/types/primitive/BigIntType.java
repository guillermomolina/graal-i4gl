package i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.compound.VarcharType;

/**
 * Type descriptor representing the longint type.
 */
public class BigIntType extends BaseType {

    public static final BigIntType SINGLETON = new BigIntType();

    private BigIntType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Long;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Long;
    }

    @Override
    public Object getDefaultValue() {
        return Long.valueOf(0);
    }

    @Override
    public Class<?> getPrimitiveClass() {
        return long.class;
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return type == SmallFloatType.SINGLETON || type == FloatType.SINGLETON || type instanceof VarcharType
                || type instanceof CharType || type == TextType.SINGLETON || type == DateType.SINGLETON;
    }

    @Override
    public String toString() {
        return "BIGINT";
    }

    @Override
    public String getNullString() {
        return " ".repeat(20);
    }
}