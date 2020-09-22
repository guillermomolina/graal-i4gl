package org.guillermomolina.i4gl.parser.identifierstable.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * Type descriptor for Turbo I4GL's Varchar type.
 */
public class VarcharDescriptor extends ArrayDescriptor {

    public VarcharDescriptor(int size) {
        super(size, CharDescriptor.getInstance());
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new VarcharValue();
    }

}