package org.guillermomolina.i4gl.runtime.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

public final class ReturnException extends ControlFlowException {

    private static final long serialVersionUID = 4073191346281369231L;

    private final Object[] results;

    public ReturnException(Object[] results) {
        this.results = results;
    }

    public Object[] getResults() {
        return results;
    }
}
