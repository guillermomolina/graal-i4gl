package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

/**
 * Representation of variables of NChar type. It is a slight wrapper to Java's {@link String}.
 */
@CompilerDirectives.ValueType
public class NCharValue implements I4GLArray {

    private String data;

    public NCharValue() {
        data = "\0";
    }

    public NCharValue(long size) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < size - 1; ++i) {
            str.append(' ');
        }
        str.append('\0');
        this.data = str.toString();
    }

    private NCharValue(NCharValue source) {
        this.data = source.data;
    }

    private NCharValue(String data) {
        this.data = data;
    }

    public void assignString(String value) {
        this.data = value + "\0";
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public Object getValueAt(int index) {
        return this.data.charAt(index);
    }

    @Override
    public void setValueAt(int index, Object value) {
        this.checkArrayIndex(index);
        this.data = this.data.substring(0, index) + value + this.data.substring(index + 1);
    }

    @Override
    public Object createDeepCopy() {
        return new NCharValue(this);
    }

    private void checkArrayIndex(int index) {
        if (index >= this.data.length()) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Creates and returns a new NChar string which is created by concatenation of two NChar strings.
     * @param left the left argument of the concatenation operation
     * @param right the right argument of the concatenation operation
     * @return the NChar string
     */
    public static NCharValue concat(NCharValue left, NCharValue right) {
        StringBuilder newData = new StringBuilder();
        newData.append(left.data);
        newData.deleteCharAt(newData.length() - 1);
        newData.append(right.data);

        return new NCharValue(newData.toString());
    }

}
