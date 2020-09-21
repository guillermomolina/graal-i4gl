package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import com.oracle.truffle.api.profiles.ConditionProfile;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing if statement.
 */
@NodeInfo(shortName = "if", description = "The node implementing a conditional statement")
public final class IfNode extends StatementNode {

	@Child
	private ExpressionNode conditionNode;
	@Child
	private StatementNode thenNode;
	@Child
	private StatementNode elseNode;
    private final ConditionProfile conditionProfile = ConditionProfile.createCountingProfile();
    private final boolean containsElseNode;

	public IfNode(ExpressionNode conditionNode, StatementNode thenNode, StatementNode elseNode) {
		this.conditionNode = conditionNode;
		this.thenNode = thenNode;
		this.elseNode = elseNode;
        containsElseNode = elseNode != null;
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		if (conditionProfile.profile(checkCondition(frame))) {
			thenNode.executeVoid(frame);
		} else {
			if (containsElseNode) {
				elseNode.executeVoid(frame);
			}
		}
	}

	private boolean checkCondition(VirtualFrame frame) {
		try {
			return conditionNode.executeInt(frame) != 0;
		} catch (UnexpectedResultException e) {
		    // This should not happen thanks to our compile time type checking
			throw new I4GLRuntimeException("The condition node provided for if is not boolean type");
		}
	}
}
