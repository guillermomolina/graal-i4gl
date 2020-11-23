package com.guillermomolina.i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLCharType;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLTextType;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLVarcharType;

/**
 * Type descriptor representing the longint type.
 */
public class I4GLBigIntType extends I4GLType {

    public static final I4GLBigIntType SINGLETON = new I4GLBigIntType();

    private I4GLBigIntType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Long;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Long;
    }

    @Override
    public Object getDefaultValue() {
        return Long.valueOf(0);
    }

    @Override
    public boolean convertibleTo(final I4GLType type) {
        return type == I4GLFloatType.SINGLETON || type instanceof I4GLVarcharType
                || type instanceof I4GLCharType || type == I4GLTextType.SINGLETON;
    }

    @Override
    public String toString() {
        return "BIGINT";
    }
}