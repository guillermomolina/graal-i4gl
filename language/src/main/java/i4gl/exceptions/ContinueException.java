package i4gl.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * This exception is thrown when a I4GL's break statement is executed. It is caught inside each loop node. Catching
 * this exception ends the loop.
 */
public class ContinueException extends ControlFlowException {
	/**
	 *
	 */
	private static final long serialVersionUID = 866615032110799531L;

	private ContinueException() {

	}

	public static ContinueException SINGLETON = new ContinueException();
}
