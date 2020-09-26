package org.guillermomolina.i4gl.runtime.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

import org.guillermomolina.i4gl.runtime.customvalues.ReturnValue;

public final class ReturnException extends ControlFlowException {

    private static final long serialVersionUID = 4073191346281369231L;

    private final ReturnValue result;

    public ReturnException(ReturnValue result) {
        this.result = result;
    }

    public ReturnValue getResult() {
        return result;
    }
}
