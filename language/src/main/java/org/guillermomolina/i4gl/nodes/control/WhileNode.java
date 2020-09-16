package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.LoopConditionProfile;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.BreakException;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing I4GL's while loop.
 */
@NodeInfo(shortName = "while", description = "The node implementing a while loop")
public class WhileNode extends StatementNode {

    private static class WhileRepeatingNode extends Node implements RepeatingNode {

        @Child
        private ExpressionNode condition;
        @Child
        private StatementNode body;

        private final LoopConditionProfile conditionProfile = LoopConditionProfile.createCountingProfile();

        private WhileRepeatingNode(ExpressionNode condition, StatementNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            try {
                if (conditionProfile.profile(condition.executeBoolean(frame))) {
                    try {
                        body.executeVoid(frame);
                    } catch (BreakException e) {
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            } catch (UnexpectedResultException e) {
                // This should not happen thanks to our compile time type checking
                throw new I4GLRuntimeException("Condition node provided to while is not boolean type");
            }
        }
    }

	@Child
    private LoopNode loopNode;

	public WhileNode(ExpressionNode condition, StatementNode body) {
	    this.loopNode = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(condition, body));
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		this.loopNode.executeLoop(frame);
	}
}
