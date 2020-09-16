package org.guillermomolina.i4gl.parser.identifierstable.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.IntConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.RealConstantDescriptor;

/**
 * Type descriptor representing the integer type.
 */
public class IntDescriptor implements OrdinalDescriptor, PrimitiveDescriptor {

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
    public int getSize() {
        return Integer.SIZE;
    }

    @Override
    public boolean containsValue(Object value) {
        return value instanceof Integer || value instanceof Long;
    }

    @Override
    public TypeDescriptor getInnerTypeDescriptor() {
        return IntDescriptor.getInstance();
    }

    @Override
    public int getFirstIndex() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == LongDescriptor.getInstance() || type == RealDescriptor.getInstance() ||
                type instanceof IntConstantDescriptor || type instanceof LongConstantDescriptor ||
                type instanceof RealConstantDescriptor;
    }

}