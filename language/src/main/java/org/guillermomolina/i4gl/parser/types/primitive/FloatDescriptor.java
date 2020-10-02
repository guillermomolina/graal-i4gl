package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the float type.
 * Uses java Double
 */
public class FloatDescriptor implements TypeDescriptor {

    public static final FloatDescriptor SINGLETON = new FloatDescriptor();

    private FloatDescriptor() {
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
    public boolean convertibleTo(final TypeDescriptor type) {
        return type instanceof VarcharDescriptor || type instanceof CharDescriptor
                || type == TextDescriptor.SINGLETON;
    }

    @Override
    public String toString() {
        return "FLOAT";
    }
}