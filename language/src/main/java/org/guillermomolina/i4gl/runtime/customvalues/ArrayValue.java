package org.guillermomolina.i4gl.runtime.customvalues;

/**
 * Interface for all array-type variables. Currently it is used only for {@link CharValue} and {@link TextValue}.
 * Other arrays are stored as Object[] or arrays of primitive types for better performance.
 */
public interface ArrayValue {

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