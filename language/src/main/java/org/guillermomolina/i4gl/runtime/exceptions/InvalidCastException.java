package org.guillermomolina.i4gl.runtime.exceptions;

import org.guillermomolina.i4gl.runtime.types.I4GLType;

public class InvalidCastException extends I4GLRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 810218821002797877L;

    public InvalidCastException(Object object, I4GLType descriptor) {
        super("Can not cast " + object.toString() + " to " + descriptor.toString());
    }
}
