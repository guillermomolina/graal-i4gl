package org.guillermomolina.i4gl.runtime.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * Exception thrown when I4GL's halt function is executed ({@HaltBuiltinNode}).
 * The exception is caught in main block's root node {@link org.guillermomolina.i4gl.nodes.root.I4GLRootNode}.
 */
public class HaltException extends ControlFlowException {

	private static final long serialVersionUID = 146173845468432542L;

	private final int exitCode;

	public HaltException(int exitCode) {
        this.exitCode = exitCode;
	}

    public int getExitCode() {
        return exitCode;
    }

}
