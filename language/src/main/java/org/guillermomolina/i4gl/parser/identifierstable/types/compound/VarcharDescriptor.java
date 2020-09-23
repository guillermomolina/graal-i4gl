package org.guillermomolina.i4gl.parser.identifierstable.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.CharConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.NCharConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.TextConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.VarcharConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
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
        return type instanceof CharConstantDescriptor || type == TextDescriptor.getInstance() || 
        type instanceof TextConstantDescriptor || type instanceof NCharDescriptor || 
        type instanceof NCharConstantDescriptor || type instanceof VarcharDescriptor || 
        type instanceof VarcharConstantDescriptor;
    }
}