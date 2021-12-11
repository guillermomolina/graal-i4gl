package i4gl.nodes.control;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;

import i4gl.exceptions.BreakException;
import i4gl.exceptions.ContinueException;
import i4gl.exceptions.I4GLRuntimeException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;

/**
 * Node representing I4GL's while loop.
 */
@NodeInfo(shortName = "WHILE", description = "The node implementing a while loop")
public class WhileNode extends StatementNode {

    private static class WhileRepeatingNode extends Node implements RepeatingNode {

        @Child
        private ExpressionNode conditionNode;
        @Child
        private StatementNode bodyNode;

        private final BranchProfile continueTaken = BranchProfile.create();
        private final BranchProfile breakTaken = BranchProfile.create();
    
        private WhileRepeatingNode(ExpressionNode conditionNode, StatementNode bodyNode) {
            this.conditionNode = conditionNode;
            this.bodyNode = bodyNode;
        }

        @Override
        public boolean executeRepeating(VirtualFrame frame) {
            if (!evaluateCondition(frame)) {
                /* Normal exit of the loop when loop condition is false. */
                return false;
            }
    
            try {
                /* Execute the loop body. */
                bodyNode.executeVoid(frame);
                /* Continue with next loop iteration. */
                return true;
    
            } catch (ContinueException ex) {
                /* In the interpreter, record profiling information that the loop uses continue. */
                continueTaken.enter();
                /* Continue with next loop iteration. */
                return true;
    
            } catch (BreakException ex) {
                /* In the interpreter, record profiling information that the loop uses break. */
                breakTaken.enter();
                /* Break out of the loop. */
                return false;
            }
        }

        private boolean evaluateCondition(VirtualFrame frame) {
            try {
                return conditionNode.executeInt(frame) != 0;
            } catch (UnexpectedResultException ex) {
                // This should not happen thanks to our compile time type checking
                throw new I4GLRuntimeException("Condition node provided to while is not boolean type");
            }
        }
    }

    @Child
    private LoopNode loopNode;

    public WhileNode(ExpressionNode conditionNode, StatementNode bodyNode) {
        this.loopNode = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(conditionNode, bodyNode));
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        loopNode.execute(frame);
    }
}
