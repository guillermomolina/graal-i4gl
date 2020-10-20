package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing if statement.
 */
@NodeInfo(shortName = "IF", description = "The node implementing a conditional statement")
public final class I4GLIfNode extends I4GLStatementNode {

	@Child
	private I4GLExpressionNode conditionNode;
	@Child
	private I4GLStatementNode thenNode;
	@Child
	private I4GLStatementNode elseNode;
    private final ConditionProfile conditionProfile = ConditionProfile.createCountingProfile();
    private final boolean containsElseNode;

	public I4GLIfNode(I4GLExpressionNode conditionNode, I4GLStatementNode thenNode, I4GLStatementNode elseNode) {
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
