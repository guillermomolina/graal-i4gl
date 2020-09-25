package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.UnexpectedRuntimeException;

/**
 * This node reads value of specified global variable (by its frame slot).
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ReadLocalVariableNodeGen}
 */
@NodeFields({
    @NodeField(name = "slot", type = FrameSlot.class),
    @NodeField(name = "typeDescriptor", type = TypeDescriptor.class)
})
public abstract class ReadGlobalVariableNode extends ExpressionNode {

	protected abstract FrameSlot getSlot();

	protected abstract TypeDescriptor getTypeDescriptor();

    @Specialization(guards = "isInt()")
    int readInt(VirtualFrame frame) {
        try {
            return getFrame(frame).getInt(getSlot());
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

	@Specialization(guards = "isLong()")
    long readLong(VirtualFrame frame) {
        try {
            return getFrame(frame).getLong(getSlot());
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization(guards = "isDouble()")
    double readDouble(VirtualFrame frame) {
        try {
            return getFrame(frame).getDouble(getSlot());
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization(guards = "isChar()")
    char readChar(VirtualFrame frame) {
        try {
            return (char) getFrame(frame).getByte(getSlot());
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization
    Object readGeneric(VirtualFrame frame) {
	    return getFrame(frame).getValue(getSlot());
    }

    @ExplodeLoop
    private VirtualFrame getFrame(VirtualFrame frame) {
        while (!frame.getFrameDescriptor().getSlots().contains(getSlot())) {
            frame = (VirtualFrame) frame.getArguments()[0];
        }

        return frame;
    }

	@Override
    public TypeDescriptor getType() {
	    return this.getTypeDescriptor();
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
