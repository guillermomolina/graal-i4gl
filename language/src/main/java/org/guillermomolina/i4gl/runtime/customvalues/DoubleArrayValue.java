package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class DoubleArrayValue extends ArrayValue {
    private final double[] array;

    public DoubleArrayValue(int size) {
        this.array = new double[size];
    }

    protected DoubleArrayValue(double[] array) {
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
        return I4GLType.DOUBLE;
    }
}
