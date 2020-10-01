package org.guillermomolina.i4gl.parser.types.compound;

import org.guillermomolina.i4gl.runtime.customvalues.TextValue;

/**
 * Type descriptor representing the string type.
 */
public class TextDescriptor extends StringDescriptor {

    public static final TextDescriptor SINGLETON = new TextDescriptor();

    private TextDescriptor() {
        super(Integer.MAX_VALUE, Char1Descriptor.SINGLETON);
    }

    @Override
    public Object getDefaultValue() {
        return new TextValue();
    }
}