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
 * Type descriptor representing the integer type.
 */
public class IntType extends BaseType {

    public static final IntType SINGLETON = new IntType();

    private IntType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Integer;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Int;
    }

    @Override
    public Object getDefaultValue() {
        return Integer.valueOf(0);
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return type == SmallIntType.SINGLETON || type == BigIntType.SINGLETON || type == FloatType.SINGLETON
                || type == SmallFloatType.SINGLETON || type instanceof VarcharType || type instanceof CharType
                || type == TextType.SINGLETON || type == DateType.SINGLETON;
    }

    @Override
    public String toString() {
        return "INT";
    }
}