package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.NullValue;

/**
 * Type descriptor representing the string type.
 */
public class CharDescriptor extends ArrayDescriptor {

    public CharDescriptor(int size) {
        super(size, Char1Descriptor.SINGLETON);
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return NullValue.SINGLETON;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == TextDescriptor.SINGLETON
                || type instanceof VarcharDescriptor || type instanceof CharDescriptor
                || type == LongDescriptor.SINGLETON || type == RealDescriptor.SINGLETON
                || type == IntDescriptor.SINGLETON;
    }
}