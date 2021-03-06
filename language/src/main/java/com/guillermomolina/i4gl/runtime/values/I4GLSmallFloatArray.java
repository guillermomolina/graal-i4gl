package com.guillermomolina.i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallFloatType;

@ExportLibrary(InteropLibrary.class)
public class I4GLSmallFloatArray extends I4GLArray {
    private final float[] array;

    public I4GLSmallFloatArray(int size) {
        this.array = new float[size];
    }

    protected I4GLSmallFloatArray(float[] array) {
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    public float getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, float value) {
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
        return I4GLSmallFloatType.SINGLETON;
    }
}
