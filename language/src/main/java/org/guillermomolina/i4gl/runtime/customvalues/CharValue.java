package org.guillermomolina.i4gl.runtime.customvalues;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;

/**
 * Representation of variables of NChar type. It is a slight wrapper to Java's
 * {@link String}.
 */
@CompilerDirectives.ValueType
public class CharValue {
    protected String data;

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

    public char getCharAt(int index) {
        return data.charAt(index);
    }

    public void setCharAt(int index, char value) {
        checkArrayIndex(index);
        data = this.data.substring(0, index) + value + this.data.substring(index + 1);
    }

    public Object createDeepCopy() {
        return new CharValue(this);
    }

    private void checkArrayIndex(int index) {
        if (index >= data.length()) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static CharValue concat(CharValue left, CharValue right) {
        throw new NotImplementedException();
    }

}
