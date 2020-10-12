package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.values.I4GLChar;

/**
 * Type descriptor representing the string type.
 */
public class CharDescriptor extends TextDescriptor {
    private final int size;

    public CharDescriptor(int size) {
        this.size = size;
    }

    @Override
    public Object getDefaultValue() {
        return new I4GLChar(size);
    }

    @Override
    public String toString() {
        return "CHAR(" + size + ")";
    }
}