package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the integer type.
 */
public class IntDescriptor implements TypeDescriptor {

    public static final IntDescriptor SINGLETON = new IntDescriptor();

    private IntDescriptor() {
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
    public boolean convertibleTo(TypeDescriptor type) {
        return type == LongDescriptor.SINGLETON || type == RealDescriptor.SINGLETON
                || type instanceof VarcharDescriptor || type instanceof CharDescriptor
                || type == TextDescriptor.SINGLETON;
    }

}