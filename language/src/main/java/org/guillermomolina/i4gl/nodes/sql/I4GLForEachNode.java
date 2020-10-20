package org.guillermomolina.i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;
import org.guillermomolina.i4gl.runtime.values.I4GLCursor;

/**
 * Node representing I4GL's foreach loop.
 */
@NodeInfo(shortName = "FOREACH", description = "The node implementing a foreach loop on a cursor")
public class I4GLForEachNode extends I4GLStatementNode {
    private final FrameSlot[] resultSlots;
    @Child
    private I4GLExpressionNode cursorVariableNode;
    @Child
    private I4GLStatementNode body;

    public I4GLForEachNode(I4GLExpressionNode cursorVariableNode, final FrameSlot[] resultSlots, final I4GLStatementNode body) {
        this.cursorVariableNode = cursorVariableNode;
        this.resultSlots = resultSlots;
        this.body = body;
    }

    public void evaluateResult(VirtualFrame frame, Object[] returnValue) {
        if (resultSlots.length != 0) {
            if (returnValue.length != resultSlots.length) {
                throw new IncorrectNumberOfReturnValuesException(resultSlots.length, returnValue.length);
            }
            for (int index = 0; index < resultSlots.length; index++) {
                final Object result = returnValue[index];
                final FrameSlot slot = resultSlots[index];
                final FrameSlotKind slotKind = frame.getFrameDescriptor().getFrameSlotKind(slot);
                switch (slotKind) {
                    case Int:
                        frame.setInt(slot, (int) result);
                        break;
                    case Long:
                        frame.setLong(slot, (long) result);
                        break;
                    case Float:
                        frame.setFloat(slot, (float) result);
                        break;
                    case Double:
                        frame.setDouble(slot, (double) result);
                        break;
                    case Object:
                        frame.setObject(slot, result);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final I4GLCursor cursor = (I4GLCursor) cursorVariableNode.executeGeneric(frame);
        cursor.start();
        while (cursor.hasNext()) {
            evaluateResult(frame, cursor.getNext());
            body.executeVoid(frame);
        }
        cursor.end();
    }
}
