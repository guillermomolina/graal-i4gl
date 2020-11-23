package com.guillermomolina.i4gl.runtime.types.primitive;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLCharType;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLTextType;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLVarcharType;

/**
 * Type descriptor representing the integer type.
 */
public class I4GLIntType extends I4GLType {

    public static final I4GLIntType SINGLETON = new I4GLIntType();

    private I4GLIntType() {
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Integer;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Int;
    }

    @Override
    public Object getDefaultValue() {
        return Integer.valueOf(0);
    }

    @Override
    public boolean convertibleTo(I4GLType type) {
        return type == I4GLBigIntType.SINGLETON || type == I4GLFloatType.SINGLETON
                || type instanceof I4GLVarcharType || type instanceof I4GLCharType
                || type == I4GLTextType.SINGLETON;
    }

    @Override 
    public String toString() {
        return "INT";
    }
}