package i4gl.exceptions;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;

public final class UndefinedNameException extends I4GLRuntimeException {

    private static final long serialVersionUID = -4386063483849151838L;

    @TruffleBoundary
    public static UndefinedNameException undefinedFunction(Node location, Object name) {
        throw new UndefinedNameException("Undefined function: " + name, location);
    }

    @TruffleBoundary
    public static UndefinedNameException undefinedProperty(Node location, Object name) {
        throw new UndefinedNameException("Undefined property: " + name, location);
    }

    private UndefinedNameException(String message, Node node) {
        super(message + " " + node.toString());
    }
}

