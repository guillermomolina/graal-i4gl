package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;

import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class SmallFloatArrayValue extends ArrayValue {
    private final float[] array;

    public SmallFloatArrayValue(int size) {
        this.array = new float[size];
    }

    protected SmallFloatArrayValue(float[] array) {
        this.array = array;
    }

    public float getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, float value) {
        array[index] = value;
    }

    @Override
    public void setObjectAt(int index, Object value) {
        setValueAt(index, (float)value);
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
