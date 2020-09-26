package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

/**
 * Representation of variables of Return type. It is a slight wrapper to Java's {@link String}.
 */
@CompilerDirectives.ValueType
public class ReturnValue implements I4GLArray {

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

    @Override
    public Object getValueAt(int index) {
        return data[index];
    }

    @Override
    public void setValueAt(int index, Object value) {
        data[index] = value;
    }

    @Override
    public Object createDeepCopy() {
        return new ReturnValue(this);
    }
}
