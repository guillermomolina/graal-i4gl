package org.guillermomolina.i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.values.I4GLNull;

public class I4GLNullType extends I4GLType {

    public static final I4GLNullType SINGLETON = new I4GLNullType();

    private I4GLNullType() {
    }    

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return library.isNull(value);
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
        return "NULL";
    }
}
