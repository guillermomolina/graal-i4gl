package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the float type.
 * Uses java Double
 */
public class DoubleDescriptor implements I4GLTypeDescriptor {

    public static final DoubleDescriptor SINGLETON = new DoubleDescriptor();

    private DoubleDescriptor() {
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
    public boolean convertibleTo(final I4GLTypeDescriptor type) {
        return type instanceof VarcharDescriptor || type instanceof CharDescriptor
                || type == TextDescriptor.SINGLETON;
    }

    @Override
    public String toString() {
        return "FLOAT";
    }
}