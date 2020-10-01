package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives;

/**
 * Representation of a string-type variable.
 */
@CompilerDirectives.ValueType
public class TextValue implements StringValue {

    private String value;

    public TextValue() {
        this.value = "";
    }

    public TextValue(String value) {
        this.value = value;
    }

    private TextValue(TextValue text) {
        this.value = text.value;
    }

    @Override
    public Object getValueAt(int index) {
        return this.value.charAt(index);
    }

    @Override
    public void setValueAt(int index, Object value) {
        char newChar = (Character) value;
        this.value = this.value.substring(0, index) + newChar + this.value.substring(++index);
    }

    @Override
    public Object createDeepCopy() {
        return new TextValue(this);
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Returns a new string whose value is the current value concatenated with
     * specified character.
     * 
     * @return the new string
     */
    public TextValue concatenate(char value) {
        return new TextValue(this.value.concat(String.valueOf(value)));
    }

    /**
     * Returns a new string whose value is the current value concatenated with
     * another string.
     * 
     * @param value the string to be appended to current value
     * @return the new string
     */
    public TextValue concatenate(TextValue value) {
        return new TextValue(this.value.concat(value.value));
    }

}
