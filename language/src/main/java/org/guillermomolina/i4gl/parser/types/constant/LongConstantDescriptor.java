package org.guillermomolina.i4gl.parser.types.constant;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;

/**
 * Type descriptor for a long-type constant. It also contains the constant's value.
 */
public class LongConstantDescriptor implements ConstantDescriptor {

    private final long value;

    /**
     * The default descriptor containing value of the constant.
     */
    public LongConstantDescriptor(long value) {
        this.value = value;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Long;
    }

    @Override
    public Object getDefaultValue() {
        return value;
    }

    @Override
    public LongConstantDescriptor negatedCopy() {
        return new LongConstantDescriptor(-value);
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean isSigned() {
        return true;
    }

    @Override
    public TypeDescriptor getType() {
        return LongDescriptor.getInstance();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return (type instanceof LongDescriptor) || (type instanceof RealConstantDescriptor) || (type instanceof RealDescriptor);
    }

}
