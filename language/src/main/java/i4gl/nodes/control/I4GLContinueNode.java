package i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.runtime.exceptions.ContinueException;
import i4gl.nodes.statement.I4GLStatementNode;

/**
 * Node representing break statement. It throws a {@link BreakException} which is then caught in a loop. Break is an
 * extension from Turbo I4GL.
 */
public final class I4GLContinueNode extends I4GLStatementNode {

	@Override
	public void executeVoid(VirtualFrame frame) {
		throw ContinueException.SINGLETON;
	}
}
