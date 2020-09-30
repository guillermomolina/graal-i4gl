package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the longint type.
 */
public class LongDescriptor implements TypeDescriptor {

    public static final LongDescriptor SINGLETON = new LongDescriptor();

    private LongDescriptor() {
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Long;
    }

    @Override
    public Object getDefaultValue() {
        return Long.valueOf(0);
    }

    @Override
    public boolean convertibleTo(final TypeDescriptor type) {
        return type == RealDescriptor.SINGLETON || type instanceof VarcharDescriptor
                || type instanceof CharDescriptor || type == TextDescriptor.SINGLETON;
    }

}