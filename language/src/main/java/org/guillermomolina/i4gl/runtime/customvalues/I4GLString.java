package org.guillermomolina.i4gl.runtime.customvalues;

/**
 * Representation of a string-type variable.
 */
public class I4GLString implements I4GLArray {

    private String value;

    public I4GLString() {
        this.value = "";
    }

    public I4GLString(String value) {
        this.value = value;
    }

    private I4GLString(I4GLString pascalString) {
        this.value = pascalString.value;
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
        return new I4GLString(this);
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Returns a new string whose value is the current value concatenated with specified character.
     * @return the new string
     */
    public I4GLString concatenate(char value) {
        return new I4GLString(this.value.concat(String.valueOf(value)));
    }

    /**
     * Returns a new string whose value is the current value concatenated with another string.
     * @param value the string to be appended to current value
     * @return the new string
     */
    public I4GLString concatenate(I4GLString value) {
        return new I4GLString(this.value.concat(value.value));
    }

}
