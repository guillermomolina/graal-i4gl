package org.guillermomolina.i4gl.parser.identifierstable.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.runtime.customvalues.SetTypeValue;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Type descriptor for I4GL's set types. It contains additional information about the universe from which it can
 * contains its values.
 */
public class SetDescriptor implements TypeDescriptor {

    private final OrdinalDescriptor baseTypeDescriptor;

    /**
     * The default constructor.
     * @param baseTypeDescriptor universe of values it can contain
     */
    public SetDescriptor(OrdinalDescriptor baseTypeDescriptor) {
        this.baseTypeDescriptor = baseTypeDescriptor;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new SetTypeValue();
    }

    /**
     * Gets the type descriptor of the universe of the values this set type can contain.
     */
    public OrdinalDescriptor getBaseTypeDescriptor() {
        return this.baseTypeDescriptor;
    }

    /**
     * Gets the type descriptor of the values this set can contain.
     */
    public TypeDescriptor getInnerType() {
        return baseTypeDescriptor.getInnerTypeDescriptor();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return false;
    }

}
