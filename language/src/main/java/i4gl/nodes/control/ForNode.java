package i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import i4gl.exceptions.I4GLRuntimeException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.logic.LessThanNodeGen;
import i4gl.nodes.logic.LessThanOrEqualNodeGen;
import i4gl.nodes.logic.NotNodeGen;
import i4gl.nodes.statement.StatementNode;

/**
 * Node representing for cycle.
 */
@NodeInfo(shortName = "FOR")
public class ForNode extends StatementNode {
    @Child
    private StatementNode assignment;
    @Child
    private StatementNode body;
    @Child
    private ExpressionNode readControlVariable;
    @Child
    private ExpressionNode initialValue;
    @Child
    private ExpressionNode finalValue;
    @Child
    private StatementNode step;

    public ForNode(final StatementNode assignment, final ExpressionNode initialValue, final ExpressionNode finalValue,
            final StatementNode step, final ExpressionNode readControlVariable, final StatementNode body) {
        this.assignment = assignment;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.step = step;
        this.body = body;
        this.readControlVariable = readControlVariable;
    }

    private void execute(final VirtualFrame frame, final ExpressionNode hasEndedNode)
            throws UnexpectedResultException {
        assignment.executeVoid(frame);

        while (hasEndedNode.executeInt(frame) != 0) {
            body.executeVoid(frame);
            step.executeVoid(frame);
        }
    }

    @Override
    public void executeVoid(final VirtualFrame frame) {
        try {
            final ExpressionNode isDescending = LessThanNodeGen.create(finalValue, initialValue);
            final ExpressionNode hasEndedNode;
            if (isDescending.executeInt(frame) != 0) {
                hasEndedNode = NotNodeGen.create(LessThanNodeGen.create(readControlVariable, finalValue));
            } else {
                hasEndedNode = LessThanOrEqualNodeGen.create(readControlVariable, finalValue);
            }
            this.execute(frame, hasEndedNode);
        } catch (final UnexpectedResultException e) {
            throw new I4GLRuntimeException(e.getMessage());
        }
    }

}