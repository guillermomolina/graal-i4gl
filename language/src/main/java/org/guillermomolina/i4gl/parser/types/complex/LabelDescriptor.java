package org.guillermomolina.i4gl.parser.types.complex;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.TypeDescriptor;

/**
 * Type descriptor for I4GL's labels. Contains additional information about the identifier of the label.
 */
public class LabelDescriptor implements TypeDescriptor {

    private final String identifier;

    public LabelDescriptor(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        return "LABEL " + identifier;
    }

}
