package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.FloatDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;

/**
 * Type descriptor representing the string type.
 */
public class TextDescriptor implements I4GLTypeDescriptor {

    public static final TextDescriptor SINGLETON = new TextDescriptor();

    protected TextDescriptor() {
    }

    @Override
    public Object getDefaultValue() {
        return "";
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public boolean convertibleTo(I4GLTypeDescriptor type) {
        return type instanceof TextDescriptor || type == IntDescriptor.SINGLETON || type == BigIntDescriptor.SINGLETON
                || type == SmallFloatDescriptor.SINGLETON || type == FloatDescriptor.SINGLETON;
    }

    @Override
    public String toString() {
        return "TEXT";
    }
}