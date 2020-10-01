package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.customvalues.CharValue;

/**
 * Type descriptor representing the string type.
 */
public class CharDescriptor extends StringDescriptor {

    public CharDescriptor(int size) {
        super(size, Char1Descriptor.SINGLETON);
    }

    @Override
    public Object getDefaultValue() {
        return new CharValue(size);
    }
}