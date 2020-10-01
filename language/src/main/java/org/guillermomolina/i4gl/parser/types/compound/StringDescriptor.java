package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;

/**
 * Type descriptor representing the string type.
 */
public abstract class StringDescriptor extends ArrayDescriptor {
    protected StringDescriptor(int size, TypeDescriptor valuesDescriptor) {
        super(size, valuesDescriptor);
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof StringDescriptor || type == LongDescriptor.SINGLETON
        || type == SmallFloatDescriptor.SINGLETON || type == IntDescriptor.SINGLETON;
    }
}