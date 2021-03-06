package com.guillermomolina.i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.values.I4GLNull;

public class I4GLObjectType extends I4GLType {

    public static final I4GLObjectType SINGLETON = new I4GLObjectType();

    private I4GLObjectType() {
    }    

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.hasMembers(value);
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return I4GLNull.SINGLETON;
    }

    @Override
    public boolean convertibleTo(final I4GLType type) {
        return false;
    }

    @Override
    public String toString() {
        return "OBJECT";
    }
}
