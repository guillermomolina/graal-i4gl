package org.guillermomolina.i4gl.runtime.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * This exception is thrown when a I4GL's break statement is executed. It is caught inside each loop node. Catching
 * this exception ends the loop.
 */
public class BreakException extends ControlFlowException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private BreakException() {

	}

	public static BreakException SINGLETON = new BreakException();
}
