package org.guillermomolina.i4gl.parser.identifierstable.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.NCharValue;

/**
 * Type descriptor for Turbo Pascal's NChar type.
 */
public class NCharDescriptor extends ArrayDescriptor {

    public NCharDescriptor(int size) {
        super(size, CharDescriptor.getInstance());
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new NCharValue();
    }

}