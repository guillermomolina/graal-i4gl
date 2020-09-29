package org.guillermomolina.i4gl.runtime.customvalues;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;

/**
 * Representation of variables of NChar type. It is a slight wrapper to Java's {@link String}.
 */
@CompilerDirectives.ValueType
public class CharValue implements ArrayValue {

    private String data;

    public CharValue(int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, ' ');
        this.data = new String(chars);
    }

    private CharValue(CharValue source) {
        this.data = source.data;
    }

    public void assignString(String value) {
        final int size = data.length();
        if (value.length() > size) {
            data = value.substring(0, size);
        } else {
            final StringBuilder str = new StringBuilder(value);
            for (int i = value.length(); i < size; ++i) {
                str.append(' ');
            }
            data = str.toString();    
        }
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
        return new CharValue(this);
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
    public static CharValue concat(CharValue left, CharValue right) {
        throw new NotImplementedException();
    }

}
