package org.guillermomolina.i4gl.parser.identifierstable.types.primitive;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.StringConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.extension.PCharDesriptor;

/**
 * Type descriptor representing the string type.
 */
public class StringDescriptor extends ArrayDescriptor implements PrimitiveDescriptor {

    private static StringDescriptor instance = new StringDescriptor();

    public static StringDescriptor getInstance() {
        return instance;
    }

    private StringDescriptor() {
        super(new OrdinalDescriptor.RangeDescriptor(new LongConstantDescriptor(0), new LongConstantDescriptor(Integer.MAX_VALUE)), CharDescriptor.getInstance());

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
        return type instanceof PCharDesriptor || type instanceof StringConstantDescriptor;
    }

}