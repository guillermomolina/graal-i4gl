package org.guillermomolina.i4gl.parser.types.complex;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

/**
 * Type descriptor for I4GL's labels. Contains additional information about the identifier of the label.
 */
public class LabelDescriptor implements I4GLTypeDescriptor {

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
    public boolean convertibleTo(I4GLTypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        return "LABEL " + identifier;
    }

}
