package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.NCharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the char type.
 */
public class CharDescriptor implements PrimitiveDescriptor {

    private static CharDescriptor instance = new CharDescriptor();

    public static CharDescriptor getInstance() {
        return instance;
    }

    private CharDescriptor() {

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Byte;
    }

    @Override
    public Object getDefaultValue() {
        return ' ';
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == TextDescriptor.getInstance() || type instanceof VarcharDescriptor
                || type instanceof NCharDescriptor;
    }

}