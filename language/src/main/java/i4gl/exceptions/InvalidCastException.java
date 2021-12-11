package i4gl.exceptions;

import i4gl.runtime.types.BaseType;

public class InvalidCastException extends I4GLRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 810218821002797877L;

    public InvalidCastException(Object object, BaseType descriptor) {
        super("Can not cast " + object.toString() + " to " + descriptor.toString());
    }
}
