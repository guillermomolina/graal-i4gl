package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.ReturnValue;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;

@NodeInfo(shortName = "call")
public final class CallNode extends I4GLStatementNode {

    private final FrameSlot[] resultSlots;
    @Child private final ExpressionNode invokeNode;

	public CallNode(ExpressionNode invokeNode, FrameSlot[] resultSlots) {
        this.invokeNode = invokeNode;
        this.resultSlots = resultSlots;
	}

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object returnValue = invokeNode.executeGeneric(frame);
        evaluateResult(frame, returnValue);
	}

    public void evaluateResult(VirtualFrame frame, ReturnValue returnValue) {
        if(returnValue.getSize() != resultSlots.length) {
            throw new IncorrectNumberOfReturnValuesException(resultSlots.length, returnValue.getSize());
        }
        try {
            for (FrameSlot resultSlot : this.resultSlots) {
                Object o = frame.getObject(resultSlot);
                throw new NotImplementedException();
            }

        } catch (FrameSlotTypeException e) {
            throw new I4GLRuntimeException("Unexpected accessing of non record type");
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
