package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.Int8Descriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.TextValue;

/**
 * Type descriptor representing the string type.
 */
public class TextDescriptor extends ArrayDescriptor {

    private static TextDescriptor instance = new TextDescriptor();

    public static TextDescriptor getInstance() {
        return instance;
    }

    private TextDescriptor() {
        super(Integer.MAX_VALUE, Int8Descriptor.getInstance());

    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new TextValue();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type == TextDescriptor.getInstance() || type instanceof VarcharDescriptor
                || type instanceof CharDescriptor || type == LongDescriptor.getInstance()
                || type == RealDescriptor.getInstance() || type == IntDescriptor.getInstance();
    }

}