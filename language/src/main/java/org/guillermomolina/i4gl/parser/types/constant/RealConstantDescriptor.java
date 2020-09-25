package org.guillermomolina.i4gl.parser.types.constant;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;

/**
 * Type descriptor for a real-type constant. It also contains the constant's value.
 */
public class RealConstantDescriptor implements ConstantDescriptor {

    private final double value;

    /**
     * The default descriptor containing value of the constant.
     */
    public RealConstantDescriptor(double value) {
        this.value = value;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Double;
    }

    @Override
    public Object getDefaultValue() {
        return this.value;
    }

    @Override
    public RealConstantDescriptor negatedCopy() {
        return new RealConstantDescriptor(-value);
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public TypeDescriptor getType() {
        return RealDescriptor.getInstance();
    }

    @Override
    public boolean isSigned() {
        return true;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == RealDescriptor.getInstance();
    }

}
