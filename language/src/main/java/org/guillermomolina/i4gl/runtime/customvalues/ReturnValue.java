package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

@CompilerDirectives.ValueType
public class ReturnValue {

    private Object[] data;

    public ReturnValue(int size) {
        this.data = new Object[size];
    }

    private ReturnValue(ReturnValue value) {
        this.data = value.data;
    }

    public int getSize() {
        return data.length;
    }

    public Object getValueAt(int index) {
        return data[index];
    }

    public void setValueAt(int index, Object value) {
        data[index] = value;
    }

    public Object createDeepCopy() {
        return new ReturnValue(this);
    }
}
