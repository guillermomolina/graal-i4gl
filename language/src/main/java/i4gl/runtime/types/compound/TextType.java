package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;

/**
 * Type descriptor representing the string type.
 */
public class TextType extends BaseType {

    public static final TextType SINGLETON = new TextType();

    protected TextType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.isString(value);
    }

    @Override
    public Object getDefaultValue() {
        return "";
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return type instanceof TextType || type == SmallIntType.SINGLETON || type == IntType.SINGLETON
                || type == BigIntType.SINGLETON || type == SmallFloatType.SINGLETON
                || type == FloatType.SINGLETON || type == DateType.SINGLETON;
    }

    @Override
    public String toString() {
        return "TEXT";
    }

    @Override
    public String getNullString() {
        return "";
    }
}