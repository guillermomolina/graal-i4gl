package org.guillermomolina.i4gl.parser.types.complex;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.DatabaseValue;

/**
 * Specialized type descriptor for text-file values.
 */
public class DatabaseDescriptor implements I4GLTypeDescriptor {
    private final String identifier;

    public DatabaseDescriptor(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new DatabaseValue();
    }

    @Override
    public boolean convertibleTo(I4GLTypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        return "DATABASE " + identifier;
    }
}
