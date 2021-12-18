package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.values.Decimal;

/**
 * Type descriptor representing the decimal type.
 * Uses java BigDecimal
 */
public class DecimalType extends BaseType {
    private final int precision;
    private final int scale;

    public DecimalType() {
        this(16);
    }

    public DecimalType(final int precision) {
        this(precision + 5, 2);
    }

    public DecimalType(final int precision, final int scale) {
        this.precision = precision;
        this.scale = scale;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Decimal;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new Decimal(this);
    }

    @Override
    public boolean convertibleTo(final BaseType type) {
        return type instanceof FloatType || type instanceof SmallFloatType;
    }

    @Override
    public String toString() {
        return "DECIMAL(" + precision + "," + scale + ")";
    }

    @Override
    public String getNullString() {
        return " ".repeat(scale + 1);
    }
}