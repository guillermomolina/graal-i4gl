package org.guillermomolina.i4gl.parser.identifierstable.types.constant;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.parser.exceptions.CantBeNegatedException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.NCharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;

/**
 * Type descriptor for a real-type constant. It also contains the constant's value.
 */
public class NCharConstantDescriptor extends ArrayDescriptor implements ConstantDescriptor {

    private final I4GLString value;

    /**
     * The default descriptor containing value of the constant.
     */
    public NCharConstantDescriptor(String value) {
        super(Integer.MAX_VALUE, CharDescriptor.getInstance());
        this.value = new I4GLString(value);
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return this.value;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor typeDescriptor) {
        return typeDescriptor instanceof NCharDescriptor;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public TypeDescriptor getType() {
        throw new NotImplementedException();
//        return NCharDescriptor.getInstance();
    }

    @Override
    public boolean isSigned() {
        return false;
    }

    @Override
    public ConstantDescriptor negatedCopy() throws LexicalException {
        throw new CantBeNegatedException();
    }
}
