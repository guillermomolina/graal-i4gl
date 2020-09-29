package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * Type descriptor representing the string type.
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
        return new VarcharValue(getSize());
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == TextDescriptor.getInstance() || type instanceof VarcharDescriptor
                || type instanceof NCharDescriptor || type == LongDescriptor.getInstance()
                || type == RealDescriptor.getInstance() || type == IntDescriptor.getInstance();
    }
}