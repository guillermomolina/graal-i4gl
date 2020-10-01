package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * Type descriptor representing the string type.
 */
public class VarcharDescriptor extends StringDescriptor {

    public VarcharDescriptor(int size) {
        super(size, Char1Descriptor.SINGLETON);
    }

    @Override
    public Object getDefaultValue() {
        return new VarcharValue(size);
    }
}