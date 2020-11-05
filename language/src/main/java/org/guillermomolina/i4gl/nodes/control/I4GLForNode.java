package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.logic.I4GLLessThanNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLLessThanOrEqualNodeGen;
import org.guillermomolina.i4gl.nodes.logic.I4GLNotNodeGen;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing for cycle.
 */
@NodeInfo(shortName = "FOR")
public class I4GLForNode extends I4GLStatementNode {
    private final FrameSlot controlSlot;
    @Child
    private I4GLStatementNode assignment;
    @Child
    private I4GLStatementNode body;
    @Child
    private I4GLExpressionNode readControlVariable;
    @Child
    private I4GLExpressionNode initialValue;
    @Child
    private I4GLExpressionNode finalValue;
    @Child
    private I4GLStatementNode step;

    public I4GLForNode(final I4GLStatementNode assignment, final FrameSlot controlSlot,
            final I4GLExpressionNode initialValue, final I4GLExpressionNode finalValue,
            final I4GLStatementNode step, final I4GLExpressionNode readControlVariable,
            final I4GLStatementNode body) {
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

    private void execute(final VirtualFrame frame, final I4GLExpressionNode hasEndedNode)
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
            I4GLExpressionNode hasEndedNode;
            if (isDescending(frame)) {
                hasEndedNode = I4GLNotNodeGen.create(I4GLLessThanNodeGen.create(readControlVariable, finalValue));
            } else {
                hasEndedNode = I4GLLessThanOrEqualNodeGen.create(readControlVariable, finalValue);
            }
            this.execute(frame, hasEndedNode);
        } catch (final UnexpectedResultException e) {
            throw new I4GLRuntimeException("Something went wrong.");
        }
    }

}