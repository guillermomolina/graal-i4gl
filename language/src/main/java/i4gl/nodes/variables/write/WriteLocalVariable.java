package i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;

@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class WriteLocalVariable extends StatementNode {

    protected abstract FrameSlot getSlot();

    protected boolean isIntOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Int || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isIntOrIllegalSlot(frame)")
    protected void writeInt(VirtualFrame frame, int value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Int);
        frame.setInt(getSlot(), value);
    }

    protected boolean isLongOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isLongOrIllegalSlot(frame)")
    protected void writeLong(VirtualFrame frame, long value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Long);
        frame.setLong(getSlot(), value);
    }

    protected boolean isFloatOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Float || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isFloatOrIllegalSlot(frame)")
    protected void writeFloat(VirtualFrame frame, float value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Float);
        frame.setFloat(getSlot(), value);
    }

    protected boolean isDoubleOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = frame.getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Double || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isDoubleOrIllegalSlot(frame)")
    protected void writeDouble(VirtualFrame frame, double value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Double);
        frame.setDouble(getSlot(), value);
    }

    @Specialization(replaces = { "writeInt", "writeLong", "writeFloat", "writeDouble" })
    protected void write(VirtualFrame frame, Object value) {
        frame.getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        frame.setObject(getSlot(), value);
    }

    public abstract void executeWrite(VirtualFrame frame, Object value);

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }

}
