package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class BigIntArrayValue extends ArrayValue {
    private final long[] array;

    public BigIntArrayValue(int size) {
        this.array = new long[size];
    }

    protected BigIntArrayValue(long[] array) {
        this.array = array;
    }

    public long getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, long value) {
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
        return I4GLType.BIGINT;
    }
}
