package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;

/**
 * Type descriptor representing the string type.
 */
public class TextDescriptor extends ArrayDescriptor {

    private static TextDescriptor instance = new TextDescriptor();

    public static TextDescriptor getInstance() {
        return instance;
    }

    private TextDescriptor() {
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
        return type == TextDescriptor.getInstance() || type instanceof VarcharDescriptor
                || type instanceof NCharDescriptor;
    }

}