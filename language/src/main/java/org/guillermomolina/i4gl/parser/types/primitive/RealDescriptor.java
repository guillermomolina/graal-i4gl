package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;

/**
 * Type descriptor representing the real type.
 */
public class RealDescriptor implements PrimitiveDescriptor {

    private static RealDescriptor instance = new RealDescriptor();

    public static RealDescriptor getInstance() {
        return instance;
    }

    private RealDescriptor() {

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Double;
    }

    @Override
    public Object getDefaultValue() {
        return 0.0d;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof VarcharDescriptor
        || type instanceof CharDescriptor || type == TextDescriptor.getInstance();
    }

}