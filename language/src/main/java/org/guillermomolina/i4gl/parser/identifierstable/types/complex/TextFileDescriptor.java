package org.guillermomolina.i4gl.parser.identifierstable.types.complex;

import org.guillermomolina.i4gl.runtime.customvalues.TextFileValue;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Specialized type descriptor for text-file values.
 */
public class TextFileDescriptor extends FileDescriptor {

    private TextFileDescriptor() {
        super(null);
    }

    private static TextFileDescriptor SINGLETON = new TextFileDescriptor();

    public static TextFileDescriptor getInstance() {
        return SINGLETON;
    }

    @Override
    public Object getDefaultValue() {
        return new TextFileValue();
    }

    @Override
    public boolean convertibleTo(TypeDescriptor typeDescriptor) {
        return super.convertibleTo(typeDescriptor) || typeDescriptor == getInstance();
    }

}
