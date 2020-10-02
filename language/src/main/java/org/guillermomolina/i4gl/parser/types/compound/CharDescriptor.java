package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.customvalues.CharValue;

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
        return new CharValue(size);
    }
}