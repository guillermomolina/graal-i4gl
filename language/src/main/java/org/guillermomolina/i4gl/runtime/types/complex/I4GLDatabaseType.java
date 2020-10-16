package org.guillermomolina.i4gl.runtime.types.complex;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.values.I4GLDatabase;

/**
 * Specialized type descriptor for text-file values.
 */
public class I4GLDatabaseType extends I4GLType {
    private final String identifier;

    public I4GLDatabaseType(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLDatabase;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new I4GLDatabase(identifier);
    }

    @Override
    public boolean convertibleTo(I4GLType type) {
        return false;
    }

    @Override
    public String toString() {
        return "DATABASE " + identifier;
    }
}
