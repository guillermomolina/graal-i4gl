package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.ReturnValue;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;

@NodeInfo(shortName = "call")
public final class CallNode extends I4GLStatementNode {

    private final FrameSlot[] resultSlots;
    @Child private ExpressionNode invokeNode;

	public CallNode(ExpressionNode invokeNode, FrameSlot[] resultSlots) {
        this.invokeNode = invokeNode;
        this.resultSlots = resultSlots;
	}

    @Override
    public void executeVoid(VirtualFrame frame) {
        ReturnValue returnValue = (ReturnValue) invokeNode.executeGeneric(frame);
        evaluateResult(frame, returnValue);
	}

    @SuppressWarnings("deprecation")
    public void evaluateResult(VirtualFrame frame, ReturnValue returnValue) {
        if(returnValue.getSize() != resultSlots.length) {
            throw new IncorrectNumberOfReturnValuesException(resultSlots.length, returnValue.getSize());
        }
        for (int index=0; index < resultSlots.length; index++) {
            final Object result = returnValue.getValueAt(index);
            final FrameSlot slot = resultSlots[index];
            switch (slot.getKind()) {
                case Int:
                    frame.setInt(slot, (int)result);
                    break;
                case Long:
                    frame.setLong(slot, (long)result);
                    break;
                case Float:
                    frame.setFloat(slot, (float)result);
                    break;
                case Double:
                    frame.setDouble(slot, (double)result);
                    break;
                case Byte:
                    frame.setByte(slot, (byte)result);
                    break;
                case Object:
                    frame.setObject(slot,result);
                    break;
                default:
            }
            }
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.CallTag.class) {
            return true;
        }
        return super.hasTag(tag);
    }    
}
