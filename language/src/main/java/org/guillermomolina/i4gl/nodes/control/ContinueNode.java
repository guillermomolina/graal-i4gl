package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.runtime.exceptions.ContinueException;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;

/**
 * Node representing break statement. It throws a {@link BreakException} which is then caught in a loop. Break is an
 * extension from Turbo I4GL.
 */
public final class ContinueNode extends StatementNode {

	@Override
	public void executeVoid(VirtualFrame frame) {
		throw ContinueException.SINGLETON;
	}
}
