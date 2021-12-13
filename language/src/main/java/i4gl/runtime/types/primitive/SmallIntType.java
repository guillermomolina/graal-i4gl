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
 * Type descriptor representing the short type.
 */
public class SmallIntType extends BaseType {

    public static final SmallIntType SINGLETON = new SmallIntType();

    private SmallIntType() {
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
    public Class<?> getPrimitiveClass() {
        return short.class;
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return type == IntType.SINGLETON || type == BigIntType.SINGLETON || type == SmallFloatType.SINGLETON
                || type == FloatType.SINGLETON || type instanceof VarcharType || type instanceof CharType
                || type == TextType.SINGLETON || type == DateType.SINGLETON;
    }

    @Override
    public String toString() {
        return "SMALLINT";
    }

    @Override
    public String getNullString() {
        return " ".repeat(6);
    }

}