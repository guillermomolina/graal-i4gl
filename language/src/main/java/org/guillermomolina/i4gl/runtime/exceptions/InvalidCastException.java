package org.guillermomolina.i4gl.runtime.exceptions;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

public class InvalidCastException extends I4GLRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 810218821002797877L;

    public InvalidCastException(Object object, I4GLTypeDescriptor descriptor) {
        super("Can not cast " + object.toString() + " to " + descriptor.toString());
    }
}
