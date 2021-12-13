package i4gl.nodes.variables.write;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
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
import i4gl.runtime.context.Context;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Varchar;

@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeField(name = "frameName", type = String.class)
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "type", type = BaseType.class)
public abstract class WriteNonLocalVariable extends StatementNode {
    @CompilationFinal
    protected VirtualFrame globalFrame;

    protected abstract String getFrameName();

    protected abstract FrameSlot getSlot();

    // TODO: Check correct type at runtime
    protected abstract BaseType getType();

    protected VirtualFrame getFrame() {
        if (globalFrame == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            globalFrame = Context.get(this).getModuleFrame(getFrameName());
        }
        return globalFrame;
    }

    protected boolean isIntOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = getFrame().getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Int || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isIntOrIllegalSlot(frame)")
    protected void writeInt(VirtualFrame frame, int value) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Int);
        getFrame().setInt(getSlot(), value);
    }

    protected boolean isLongOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = getFrame().getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isLongOrIllegalSlot(frame)")
    protected void writeLong(VirtualFrame frame, long value) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Long);
        getFrame().setLong(getSlot(), value);
    }

    protected boolean isFloatOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = getFrame().getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Float || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isFloatOrIllegalSlot(frame)")
    protected void writeFloat(VirtualFrame frame, float value) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Float);
        getFrame().setFloat(getSlot(), value);
    }

    protected boolean isDoubleOrIllegalSlot(VirtualFrame frame) {
        final FrameSlotKind kind = getFrame().getFrameDescriptor().getFrameSlotKind(getSlot());
        return kind == FrameSlotKind.Double || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isDoubleOrIllegalSlot(frame)")
    protected void writeDouble(VirtualFrame frame, double value) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Double);
        getFrame().setDouble(getSlot(), value);
    }

    protected boolean isChar() {
        return getType() instanceof CharType;
    }

    @Specialization(guards = "isChar()")
    protected void writeChar(VirtualFrame frame, String string) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        Char value = (Char)getType().getDefaultValue();
        value.assignString(string);
        getFrame().setObject(getSlot(), value);
    }

    protected boolean isVarchar() {
        return getType() instanceof VarcharType;
    }

    @Specialization(guards = "isVarchar()")
    protected void writeVarchar(VirtualFrame frame, String string) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        Varchar value = (Varchar) getType().getDefaultValue();
        value.assignString(string);
        getFrame().setObject(getSlot(), value);
    }

    @Specialization(replaces = { "writeInt", "writeLong", "writeFloat", "writeDouble", "writeChar", "writeVarchar" })
    protected void write(VirtualFrame frame, Object value) {
        getFrame().getFrameDescriptor().setFrameSlotKind(getSlot(), FrameSlotKind.Object);
        getFrame().setObject(getSlot(), value);
    }

    public abstract void executeWrite(VirtualFrame frame, Object value);

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }

}
