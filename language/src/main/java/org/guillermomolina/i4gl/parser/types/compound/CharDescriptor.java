package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.CharValue;

/**
 * Type descriptor representing the string type.
 */
public class CharDescriptor extends ArrayDescriptor {

    public CharDescriptor(int size) {
        super(size, Char1Descriptor.getInstance());
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new CharValue(getSize());
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == TextDescriptor.getInstance()
                || type instanceof VarcharDescriptor || type instanceof CharDescriptor
                || type == LongDescriptor.getInstance() || type == RealDescriptor.getInstance()
                || type == IntDescriptor.getInstance();
    }
}