package org.guillermomolina.i4gl.parser.identifierstable.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.BooleanConstantDescriptor;

/**
 * Type descriptor representing the boolean type.
 */
public class BooleanDescriptor implements OrdinalDescriptor, PrimitiveDescriptor {

    private static BooleanDescriptor instance = new BooleanDescriptor();

    public static BooleanDescriptor getInstance() {
        return instance;
    }

    private BooleanDescriptor() {

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Boolean;
    }

    @Override
    public Object getDefaultValue() {
        return false;
    }

    @Override
    public int getFirstIndex() {
        return 0;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public boolean containsValue(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public TypeDescriptor getInnerTypeDescriptor() {
        return BooleanDescriptor.getInstance();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof BooleanConstantDescriptor;
    }

}