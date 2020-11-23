package com.guillermomolina.i4gl.runtime.types.complex;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.values.I4GLCursor;

/**
 * Specialized type descriptor for text-file values.
 */
public class I4GLCursorType extends I4GLType {

    public static final I4GLCursorType SINGLETON = new I4GLCursorType();

    private I4GLCursorType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLCursor;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        throw new NotImplementedException();
    }

    @Override
    public boolean convertibleTo(I4GLType type) {
        return false;
    }

    @Override
    public String toString() {
        return "CURSOR";
    }
}
