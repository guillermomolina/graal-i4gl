package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;

/**
 * This node reads value of specified local variable (by its frame slot).
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle. {@link ReadLocalVariableNodeGen}
 */
@NodeFields({ 
    @NodeField(name = "slot", type = FrameSlot.class),
    @NodeField(name = "type", type = I4GLType.class)
})
public abstract class I4GLReadLocalVariableNode extends I4GLExpressionNode {

    protected abstract FrameSlot getSlot();

    @Specialization(guards = "frame.isInt(getSlot())")
    protected int readInt(final VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, getSlot());
    }

    @Specialization(guards = "frame.isLong(getSlot())")
    protected long readBigInt(final VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, getSlot());
    }

    @Specialization(guards = "frame.isFloat(getSlot())")
    protected float readSmallFloat(final VirtualFrame frame) {
        return FrameUtil.getFloatSafe(frame, getSlot());
    }

    @Specialization(guards = "frame.isDouble(getSlot())")
    protected double readFloat(final VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, getSlot());
    }

    @Specialization(replaces = { "readInt", "readBigInt", "readSmallFloat", "readFloat" })
    protected Object readObject(final VirtualFrame frame) {
        if (!frame.isObject(getSlot())) {
            /*
             * The FrameSlotKind has been set to Object, so from now on all writes to the
             * local variable will be Object writes. However, now we are in a frame that
             * still has an old non-Object value. This is a slow-path operation: we read the
             * non-Object value, and write it immediately as an Object value so that we do
             * not hit this path again multiple times for the same variable of the same
             * frame.
             */
            CompilerDirectives.transferToInterpreter();
            final Object result = frame.getValue(getSlot());
            frame.setObject(getSlot(), result);
            return result;
        }

        return FrameUtil.getObjectSafe(frame, getSlot());
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
