package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.*;

import com.oracle.truffle.api.profiles.LoopConditionProfile;
import org.guillermomolina.i4gl.runtime.exceptions.BreakException;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing I4GL's repeat loop.
 */
@NodeInfo(shortName = "repeat", description = "The node implementing a repeat loop")
public class RepeatNode extends StatementNode {

    private static class RepeatRepeatingNode extends Node implements RepeatingNode {

        @Child
        private ExpressionNode condition;
        @Child
        private StatementNode body;

        private final LoopConditionProfile conditionProfile = LoopConditionProfile.createCountingProfile();

        private RepeatRepeatingNode(ExpressionNode condition, StatementNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            try {
                body.executeVoid(frame);
                return !conditionProfile.profile(condition.executeBoolean(frame));
            } catch(BreakException e) {
                return false;
            } catch (UnexpectedResultException e) {
                // This should not happen thanks to our compile time type checking
                throw new I4GLRuntimeException("Condition node provided to repeat is not boolean type");
            }
        }
    }

	@Child
    private LoopNode loopNode;

	public RepeatNode(ExpressionNode condition, StatementNode body) {
		this.loopNode = Truffle.getRuntime().createLoopNode(new RepeatRepeatingNode(condition, body));
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		this.loopNode.executeLoop(frame);
	}
}
