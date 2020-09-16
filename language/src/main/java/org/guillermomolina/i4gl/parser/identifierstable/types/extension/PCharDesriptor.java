package org.guillermomolina.i4gl.parser.identifierstable.types.extension;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.runtime.customvalues.PCharValue;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;

/**
 * Type descriptor for Turbo I4GL's PChar type.
 */
public class PCharDesriptor extends ArrayDescriptor {

    private static PCharDesriptor instance = new PCharDesriptor();

    private PCharDesriptor() {
        super(new OrdinalDescriptor.RangeDescriptor(new LongConstantDescriptor(0), new LongConstantDescriptor(Integer.MAX_VALUE)), CharDescriptor.getInstance());
    }

    public static PCharDesriptor getInstance() {
        return instance;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new PCharValue();
    }

}