package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.logic.I4GLLessThanNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLLessThanOrEqualNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLNotNodeGen;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLSimpleAssignmentNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing for cycle.
 */
@NodeInfo(shortName = "for")
public class I4GLForNode extends I4GLStatementNode {
    private final FrameSlot controlSlot;
    @Child
    private I4GLSimpleAssignmentNode assignment;
    @Child
    private I4GLStatementNode body;
    @Child
    private I4GLExpressionNode hasEndedAscendingNode;
    @Child
    private I4GLExpressionNode hasEndedDescendingNode;
    @Child
    private I4GLExpressionNode initialValue;
    @Child
    private I4GLExpressionNode finalValue;
    @Child
    private I4GLSimpleAssignmentNode step;

    public I4GLForNode(final I4GLSimpleAssignmentNode assignment, final FrameSlot controlSlot,
            final I4GLExpressionNode initialValue, final I4GLExpressionNode finalValue, final I4GLSimpleAssignmentNode step,
            final I4GLExpressionNode readControlVariableNode, final I4GLStatementNode body) {
        this.assignment = assignment;
        this.controlSlot = controlSlot;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.step = step;
        this.body = body;
        this.hasEndedAscendingNode = I4GLLessThanOrEqualNodeGen.create(readControlVariableNode, finalValue);
        this.hasEndedDescendingNode = I4GLNotNodeGen.create(I4GLLessThanNodeGen.create(readControlVariableNode, finalValue));
    }

    @Override
    public void executeVoid(final VirtualFrame frame) {
        try {
            final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(controlSlot);
            I4GLExpressionNode hasEndedNode = hasEndedAscendingNode;
            switch (kind) {
                case Int:
                    if (this.initialValue.executeInt(frame) >= this.finalValue.executeInt(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                case Long:
                    if (this.initialValue.executeBigInt(frame) >= this.finalValue.executeBigInt(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                case Float:
                    if (this.initialValue.executeSmallFloat(frame) >= this.finalValue.executeSmallFloat(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                case Double:
                    if (this.initialValue.executeFloat(frame) >= this.finalValue.executeFloat(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                default:
                    throw new I4GLRuntimeException("Unsupported control variable type");
            }

            this.execute(frame, hasEndedNode);
        } catch (final UnexpectedResultException e) {
            throw new I4GLRuntimeException("Something went wrong.");
        }
    }

    private void execute(final VirtualFrame frame, final I4GLExpressionNode hasEndedNode) throws UnexpectedResultException {
        assignment.executeVoid(frame);

        while (hasEndedNode.executeInt(frame) != 0) {
            body.executeVoid(frame);
            step.executeVoid(frame);
        }
    }
}