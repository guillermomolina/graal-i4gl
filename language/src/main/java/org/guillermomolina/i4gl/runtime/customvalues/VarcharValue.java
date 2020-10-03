package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.runtime.exceptions.IndexOutOfBoundsException;

/**
 * Representation of variables of Varchar type. It is a slight wrapper to Java's {@link String}.
 */
@CompilerDirectives.ValueType
public class VarcharValue {

    protected String data;
    private final int size;

    public VarcharValue(int size) {
        this.size = size;
        this.data = "";
    }

    private VarcharValue(VarcharValue source) {
        this.size = source.size;
        this.data = source.data;
    }

    public void assignString(String value) {
        data = value.substring(0, Math.min(size, value.length()));
    }

    public char getCharAt(int index) {
        checkArrayIndex(index);
        return data.charAt(index);
    }

    public void setCharAt(int index, char value) {
        checkArrayIndex(index);
        if(index > data.length()) {
            final StringBuilder str = new StringBuilder(data);
            for (int i = data.length(); i < index; ++i) {
                str.append(' ');
            }
            str.append(value);
            data = str.toString();    
        }
        else {
            data = data.substring(0, index) + value + data.substring(index + 1);
        }
    }

    public Object createDeepCopy() {
        return new VarcharValue(this);
    }

    private void checkArrayIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static VarcharValue concat(VarcharValue left, VarcharValue right) {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return data;
    }
}
