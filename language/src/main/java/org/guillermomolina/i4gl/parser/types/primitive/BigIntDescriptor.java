package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the longint type.
 */
public class BigIntDescriptor implements TypeDescriptor {

    public static final BigIntDescriptor SINGLETON = new BigIntDescriptor();

    private BigIntDescriptor() {
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
        return type == FloatDescriptor.SINGLETON || type instanceof VarcharDescriptor
                || type instanceof CharDescriptor || type == TextDescriptor.SINGLETON;
    }

    @Override
    public String toString() {
        return "BIGINT";
    }
}