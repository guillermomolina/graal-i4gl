package org.guillermomolina.i4gl.parser.identifierstable.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.StringConstantDescriptor;

/**
 * Type descriptor representing the string type.
 */
public class TextDescriptor extends ArrayDescriptor implements PrimitiveDescriptor {

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
        return type instanceof VarcharDescriptor || type instanceof StringConstantDescriptor;
    }

}