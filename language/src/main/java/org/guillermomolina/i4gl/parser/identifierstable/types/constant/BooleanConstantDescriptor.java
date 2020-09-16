package org.guillermomolina.i4gl.parser.identifierstable.types.constant;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.parser.exceptions.CantBeNegatedException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.BooleanDescriptor;

/**
 * Type descriptor for a boolean-type constant. It also contains the constant's value.
 */
public class BooleanConstantDescriptor implements OrdinalConstantDescriptor {

    private final boolean value;

    /**
     * The default descriptor containing value of the constant.
     */
    public BooleanConstantDescriptor(boolean value) {
        this.value = value;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Boolean;
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
        return false;
    }

    @Override
    public ConstantDescriptor negatedCopy() throws LexicalException {
        throw new CantBeNegatedException();
    }

    @Override
    public int getOrdinalValue() {
        return (value)? 1 : 0;
    }

    @Override
    public TypeDescriptor getType() {
        return BooleanDescriptor.getInstance();
    }

    @Override
    public TypeDescriptor getInnerType() {
        return this.getType();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof BooleanDescriptor;
    }

}
