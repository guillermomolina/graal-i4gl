package org.guillermomolina.i4gl.parser.types.constant;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.exceptions.ParseException;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.CantBeNegatedException;

/**
 * Type descriptor for a char-type constant. It also contains the constant's value.
 */
public class CharConstantDescriptor implements ConstantDescriptor {

    private final char value;

    /**
     * The default descriptor containing value of the constant.
     */
    public CharConstantDescriptor(char value) {
        this.value = value;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Byte;
    }

    @Override
    public Object getDefaultValue() {
        return this.value;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor typeDescriptor) {
        return typeDescriptor == CharDescriptor.getInstance();
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
    public ConstantDescriptor negatedCopy() throws ParseException {
        throw new CantBeNegatedException();
    }

    @Override
    public TypeDescriptor getType() {
        return CharDescriptor.getInstance();
    }

}
