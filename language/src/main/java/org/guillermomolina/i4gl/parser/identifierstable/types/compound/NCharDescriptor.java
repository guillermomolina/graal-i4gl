package org.guillermomolina.i4gl.parser.identifierstable.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.CharConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.NCharConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.TextConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.VarcharConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;

/**
 * Type descriptor representing the string type.
 */
public class NCharDescriptor extends ArrayDescriptor {

    private static NCharDescriptor instance = new NCharDescriptor();

    public static NCharDescriptor getInstance() {
        return instance;
    }

    private NCharDescriptor() {
        super(Integer.MAX_VALUE, CharDescriptor.getInstance());

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new I4GLString();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof CharConstantDescriptor || type == TextDescriptor.getInstance() || 
        type instanceof TextConstantDescriptor || type == VarcharDescriptor.getInstance() || 
        type instanceof VarcharConstantDescriptor || type == NCharDescriptor.getInstance() || 
        type instanceof NCharConstantDescriptor;
    }

}