package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;

/**
 * Node representing case statement.
 */
public class CaseNode extends I4GLStatementNode {

	@Children
	private final ExpressionNode[] caseExpressions;
	@Children
	private final I4GLStatementNode[] caseStatements;
	@Child
	private ExpressionNode caseValue;
	@Child
	private I4GLStatementNode elseBranch;

	public CaseNode(ExpressionNode caseValue, ExpressionNode[] caseExpressions, I4GLStatementNode[] caseStatements,
			I4GLStatementNode elseBranch) {
		this.caseExpressions = caseExpressions;
		this.caseStatements = caseStatements;
		this.caseValue = caseValue;
		this.elseBranch = elseBranch;
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		Object value = caseValue.executeGeneric(frame);

		for (int i = 0; i < caseExpressions.length; i++) {
			if (caseExpressions[i].executeGeneric(frame).equals(value)) {
				caseStatements[i].executeVoid(frame);
				return;
			}
		}

		if (elseBranch != null) {
			elseBranch.executeVoid(frame);
		}
	}
}
