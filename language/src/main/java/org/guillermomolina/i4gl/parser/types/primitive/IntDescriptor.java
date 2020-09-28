package org.guillermomolina.i4gl.parser.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.IntConstantDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.RealConstantDescriptor;

/**
 * Type descriptor representing the integer type.
 */
public class IntDescriptor implements PrimitiveDescriptor {

    private static IntDescriptor instance = new IntDescriptor();

    public static IntDescriptor getInstance() {
        return instance;
    }

    private IntDescriptor() {

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Int;
    }

    @Override
    public Object getDefaultValue() {
        return 0;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == LongDescriptor.getInstance() || type == RealDescriptor.getInstance() ||
                type instanceof IntConstantDescriptor || type instanceof LongConstantDescriptor ||
                type instanceof RealConstantDescriptor || type instanceof VarcharDescriptor;
    }

}