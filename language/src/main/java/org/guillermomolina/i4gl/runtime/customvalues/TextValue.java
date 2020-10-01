package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

/**
 * Representation of a string-type variable.
 */
@CompilerDirectives.ValueType
public class TextValue {

    protected String data;

    public TextValue() {
        this.data = "";
    }

    public TextValue(String value) {
        this.data = value;
    }

    private TextValue(TextValue text) {
        this.data = text.data;
    }

    public Object getValueAt(int index) {
        return data.charAt(index);
    }

    public void setValueAt(int index, Object value) {
        char newChar = (Character) value;
        data = data.substring(0, index) + newChar + data.substring(++index);
    }

    public Object createDeepCopy() {
        return new TextValue(this);
    }

    @Override
    public String toString() {
        return data;
    }

    public TextValue concatenate(char value) {
        return new TextValue(data.concat(String.valueOf(value)));
    }

    public TextValue concatenate(TextValue value) {
        return new TextValue(data.concat(value.data));
    }

}
