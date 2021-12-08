package i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;

/**
 * Node representing case statement.
 */
public class CaseNode extends StatementNode {

	@Children
	private final ExpressionNode[] caseExpressions;
	@Children
	private final StatementNode[] caseStatements;
	@Child
	private ExpressionNode caseValue;
	@Child
	private StatementNode elseBranch;

	public CaseNode(ExpressionNode caseValue, ExpressionNode[] caseExpressions, StatementNode[] caseStatements,
			StatementNode elseBranch) {
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
