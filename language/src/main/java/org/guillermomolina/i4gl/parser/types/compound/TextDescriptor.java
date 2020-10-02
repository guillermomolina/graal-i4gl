package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.TextValue;

/**
 * Type descriptor representing the string type.
 */
public class TextDescriptor implements TypeDescriptor {

    public static final TextDescriptor SINGLETON = new TextDescriptor();

    protected TextDescriptor() {
    }

    @Override
    public Object getDefaultValue() {
        return new TextValue();
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return type instanceof TextDescriptor || type == IntDescriptor.SINGLETON || type == LongDescriptor.SINGLETON
                || type == SmallFloatDescriptor.SINGLETON || type == RealDescriptor.SINGLETON;
    }
}