package org.guillermomolina.i4gl.parser.identifierstable.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.RealConstantDescriptor;

/**
 * Type descriptor representing the longint type.
 */
public class LongDescriptor implements OrdinalDescriptor, PrimitiveDescriptor {

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
    public int getSize() {
        return Integer.SIZE;
    }

    @Override
    public boolean containsValue(Object value) {
        return value instanceof Integer || value instanceof Long;
    }

    @Override
    public TypeDescriptor getInnerTypeDescriptor() {
        return LongDescriptor.getInstance();
    }

    @Override
    public int getFirstIndex() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == RealDescriptor.getInstance() || type instanceof LongConstantDescriptor ||
                type instanceof RealConstantDescriptor;
    }

}