package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the real type.
 */
public class SmallFloatDescriptor implements TypeDescriptor {

    public static final SmallFloatDescriptor SINGLETON = new SmallFloatDescriptor();

    private SmallFloatDescriptor() {
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Float;
    }

    @Override
    public Object getDefaultValue() {
        return Float.valueOf(0.0f);
    }

    @Override
    public boolean convertibleTo(final TypeDescriptor type) {
        return type instanceof VarcharDescriptor
        || type instanceof CharDescriptor || type == TextDescriptor.SINGLETON;
    }

    @Override
    public String toString() {
        return "SMALLFLOAT";
    }

}