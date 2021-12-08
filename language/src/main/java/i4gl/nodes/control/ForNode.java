package i4gl.nodes.control;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.logic.LessThanNodeGen;
import i4gl.nodes.logic.LessThanOrEqualNodeGen;
import i4gl.nodes.logic.NotNodeGen;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing for cycle.
 */
@NodeInfo(shortName = "FOR")
public class ForNode extends StatementNode {
    private final FrameSlot controlSlot;
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

    public ForNode(final StatementNode assignment, final FrameSlot controlSlot,
            final ExpressionNode initialValue, final ExpressionNode finalValue,
            final StatementNode step, final ExpressionNode readControlVariable,
            final StatementNode body) {
        this.assignment = assignment;
        this.controlSlot = controlSlot;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.step = step;
        this.body = body;
        this.readControlVariable = readControlVariable;
    }

    private boolean isDescending(final VirtualFrame frame) throws UnexpectedResultException {
        final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(controlSlot);
        switch (kind) {
            case Int:
                return this.finalValue.executeInt(frame) < this.initialValue.executeInt(frame);
            case Long:
                return this.finalValue.executeBigInt(frame) < this.initialValue.executeBigInt(frame);
            case Float:
                return this.finalValue.executeSmallFloat(frame) < this.initialValue.executeSmallFloat(frame);
            case Double:
                return this.finalValue.executeDouble(frame) < this.initialValue.executeDouble(frame);
            default:
                throw new I4GLRuntimeException("Unsupported control variable type");
        }
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
            ExpressionNode hasEndedNode;
            if (isDescending(frame)) {
                hasEndedNode = NotNodeGen.create(LessThanNodeGen.create(readControlVariable, finalValue));
            } else {
                hasEndedNode = LessThanOrEqualNodeGen.create(readControlVariable, finalValue);
            }
            this.execute(frame, hasEndedNode);
        } catch (final UnexpectedResultException e) {
            throw new I4GLRuntimeException("Something went wrong.");
        }
    }

}