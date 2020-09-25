package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.constant.CharConstantDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.NCharValue;

/**
 * Type descriptor representing the string type.
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
        return new NCharValue(getSize());
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof CharConstantDescriptor || type == TextDescriptor.getInstance() || 
        type instanceof VarcharDescriptor || type instanceof NCharDescriptor;
    }

}