package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.statement.StatementNode;

/**
 * Node representing body of a procedure. It contains a body node which is executed along with this node.
 */
@NodeInfo(shortName = "procedure body")
public final class ProcedureBodyNode extends StatementNode {

	@Child
	private StatementNode bodyNode;

	public ProcedureBodyNode(StatementNode bodyNode) {
		this.bodyNode = bodyNode;
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		bodyNode.executeVoid(frame);
	}
}
