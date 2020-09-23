package org.guillermomolina.i4gl.parser.identifierstable.types.complex;

import org.guillermomolina.i4gl.runtime.customvalues.DatabaseValue;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Specialized type descriptor for text-file values.
 */
public class DatabaseDescriptor implements TypeDescriptor {
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
    public boolean convertibleTo(TypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        return identifier;
    }

}
