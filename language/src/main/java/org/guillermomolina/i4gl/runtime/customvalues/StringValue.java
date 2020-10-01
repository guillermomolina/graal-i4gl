package org.guillermomolina.i4gl.runtime.customvalues;

/**
 * Interface for all string-type variables..
 */
public interface StringValue {

    /**
     * Gets value at specified index.
     * @param index the index
     */
    Object getValueAt(int index);

    /**
     * Sets value at specified index.
     * @param index the index
     * @param value the value
     */
    void setValueAt(int index, Object value);

    /**
     * Creates a deep copy of the array.
     */
    Object createDeepCopy();

}