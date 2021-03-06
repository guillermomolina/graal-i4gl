package com.guillermomolina.i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLFloatType;

@ExportLibrary(InteropLibrary.class)
public class I4GLFloatArray extends I4GLArray {
    private final double[] array;

    public I4GLFloatArray(int size) {
        this.array = new double[size];
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    protected I4GLFloatArray(double[] array) {
        this.array = array;
    }

    public double getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, double value) {
        array[index] = value;
    }
    
    @Override
    protected Object getArray() {
        return array;
    }

    @Override
    public int getSize() {
        return array.length;
    }

    @Override
    public I4GLType getElementType() {
        return I4GLFloatType.SINGLETON;
    }
}
