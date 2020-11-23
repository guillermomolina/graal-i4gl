package com.guillermomolina.i4gl.runtime.types.primitive;

import java.math.BigDecimal;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.values.I4GLDecimal;

/**
 * Type descriptor representing the decimal type.
 * Uses java BigDecimal
 */
public class I4GLDecimalType extends I4GLType {
    private final int precision;
    private final int scale;

    public I4GLDecimalType(final int precision, final int scale) {
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLDecimal;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        BigDecimal value = BigDecimal.ZERO.setScale(scale);
        return new I4GLDecimal(value);
    }

    @Override
    public boolean convertibleTo(final I4GLType type) {
        return type instanceof I4GLFloatType || type instanceof I4GLSmallFloatType;
    }

    @Override
    public String toString() {
        return "DECIMAL(" + precision + "," + scale + ")";
    }
}