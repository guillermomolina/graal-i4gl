package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.NCharDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.RealConstantDescriptor;

/**
 * Type descriptor representing the longint type.
 */
public class LongDescriptor implements PrimitiveDescriptor {

    private static LongDescriptor instance = new LongDescriptor();

    public static LongDescriptor getInstance() {
        return instance;
    }

    private LongDescriptor() {

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Long;
    }

    @Override
    public Object getDefaultValue() {
        return 0L;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == RealDescriptor.getInstance() || type instanceof LongConstantDescriptor ||
                type instanceof RealConstantDescriptor || type instanceof VarcharDescriptor
                || type instanceof NCharDescriptor || type == TextDescriptor.getInstance();
    }

}