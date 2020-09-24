package org.guillermomolina.i4gl.parser.identifierstable.types.constant;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.IntDescriptor;

/**
 * Type descriptor for a integer-type constant. It also contains the constant's value.
 */
public class IntConstantDescriptor implements ConstantDescriptor {

    private final int value;

    /**
     * The default descriptor containing value of the constant.
     */
    public IntConstantDescriptor(int value) {
        this.value = value;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Int;
    }

    @Override
    public Object getDefaultValue() {
        return value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean isSigned() {
        return true;
    }

    @Override
    public ConstantDescriptor negatedCopy() throws LexicalException {
        return new IntConstantDescriptor(-this.value);
    }

    @Override
    public TypeDescriptor getType() {
        return IntDescriptor.getInstance();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof IntDescriptor;
    }

}
