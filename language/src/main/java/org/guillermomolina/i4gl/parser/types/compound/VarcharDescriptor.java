package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * Type descriptor representing the string type.
 */
public class VarcharDescriptor extends TextDescriptor {
    private final int size;

    public VarcharDescriptor(int size) {
        this.size = size;
    }

    @Override
    public Object getDefaultValue() {
        return new VarcharValue(size);
    }

    @Override
    public String toString() {
        return "VARCHAR(" + size + ")";
    }

}