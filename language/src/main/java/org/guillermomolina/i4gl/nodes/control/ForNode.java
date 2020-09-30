package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.logic.LessThanNodeGen;
import org.guillermomolina.i4gl.nodes.logic.LessThanOrEqualNodeGen;
import org.guillermomolina.i4gl.nodes.logic.NotNodeGen;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.write.SimpleAssignmentNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing for cycle.
 */
@NodeInfo(shortName = "for")
public class ForNode extends I4GLStatementNode {
    private final FrameSlot controlSlot;
    @Child
    private SimpleAssignmentNode assignment;
    @Child
    private I4GLStatementNode body;
    @Child
    private ExpressionNode hasEndedAscendingNode;
    @Child
    private ExpressionNode hasEndedDescendingNode;
    @Child
    private ExpressionNode initialValue;
    @Child
    private ExpressionNode finalValue;
    @Child
    private SimpleAssignmentNode step;

    public ForNode(SimpleAssignmentNode assignment, FrameSlot controlSlot, ExpressionNode initialValue, ExpressionNode finalValue,
    SimpleAssignmentNode step, ExpressionNode readControlVariableNode, I4GLStatementNode body) {
        this.assignment = assignment;
        this.controlSlot = controlSlot;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.step = step;
        this.body = body;
        this.hasEndedAscendingNode = LessThanOrEqualNodeGen.create(readControlVariableNode, finalValue);
        this.hasEndedDescendingNode = NotNodeGen.create(LessThanNodeGen.create(readControlVariableNode, finalValue));
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try {
            final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(controlSlot);
            ExpressionNode hasEndedNode = hasEndedAscendingNode;
            switch (kind) {
                case Int:
                    if (this.initialValue.executeInt(frame) >= this.finalValue.executeInt(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                case Long:
                    if (this.initialValue.executeLong(frame) >= this.finalValue.executeLong(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                case Float:
                    if (this.initialValue.executeFloat(frame) >= this.finalValue.executeFloat(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                case Double:
                    if (this.initialValue.executeDouble(frame) >= this.finalValue.executeDouble(frame)) {
                        hasEndedNode = hasEndedDescendingNode;
                    }
                    break;
                default:
                    throw new I4GLRuntimeException("Unsupported control variable type");
            }

            this.execute(frame, hasEndedNode);
        } catch (UnexpectedResultException e) {
            throw new I4GLRuntimeException("Something went wrong.");
        }
    }

    private void execute(VirtualFrame frame, ExpressionNode hasEndedNode) throws UnexpectedResultException {
        assignment.executeVoid(frame);

        while (hasEndedNode.executeInt(frame) != 0) {
            body.executeVoid(frame);
            step.executeVoid(frame);
        }
    }
}