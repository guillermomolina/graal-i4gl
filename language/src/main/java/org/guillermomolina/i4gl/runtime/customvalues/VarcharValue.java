package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.runtime.exceptions.IndexOutOfBoundsException;

/**
 * Representation of variables of Varchar type. It is a slight wrapper to Java's {@link String}.
 */
@CompilerDirectives.ValueType
public class VarcharValue extends TextValue {

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

    @Override
    public Object getValueAt(int index) {
        checkArrayIndex(index);
        return data.charAt(index);
    }

    @Override
    public void setValueAt(int index, Object value) {
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

    @Override
    public Object createDeepCopy() {
        return new VarcharValue(this);
    }

    private void checkArrayIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Creates and returns a new Varchar string which is created by concatenation of two Varchar strings.
     * @param left the left argument of the concatenation operation
     * @param right the right argument of the concatenation operation
     * @return the Varchar string
     */
    public static VarcharValue concat(VarcharValue left, VarcharValue right) {
        throw new NotImplementedException();
    }

}
