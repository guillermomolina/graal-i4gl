package org.guillermomolina.i4gl.runtime.values;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class I4GLFloatArray extends I4GLArrayValue {
    private final double[] array;

    public I4GLFloatArray(int size) {
        this.array = new double[size];
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
        return I4GLType.FLOAT;
    }
}
