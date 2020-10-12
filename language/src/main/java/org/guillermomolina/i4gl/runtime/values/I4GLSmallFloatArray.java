package org.guillermomolina.i4gl.runtime.values;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class I4GLSmallFloatArray extends I4GLArrayValue {
    private final float[] array;

    public I4GLSmallFloatArray(int size) {
        this.array = new float[size];
    }

    protected I4GLSmallFloatArray(float[] array) {
        this.array = array;
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
        return I4GLType.SMALLFLOAT;
    }
}
