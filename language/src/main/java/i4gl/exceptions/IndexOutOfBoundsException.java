package i4gl.exceptions;

import com.oracle.truffle.api.nodes.Node;

/**
 * Exception thrown when user is accessing an array at non-existing index,
 */
public class IndexOutOfBoundsException extends I4GLRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -2867470753887611077L;

    public IndexOutOfBoundsException(int  index) {
        super("Index (" + index + ") out of bounds.");
    }

    public IndexOutOfBoundsException(Node node) {
        super("Index out of bounds at " + node.toString());
    }

}
