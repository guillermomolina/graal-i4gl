package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.values.I4GLVarchar;

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
        return new I4GLVarchar(size);
    }

    @Override
    public String toString() {
        return "VARCHAR(" + size + ")";
    }

}