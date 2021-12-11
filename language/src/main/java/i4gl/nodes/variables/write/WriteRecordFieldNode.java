package i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Record;

@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
@NodeChild(value = "recordNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
public abstract class WriteRecordFieldNode extends StatementNode {

    protected abstract String getIdentifier();
    protected abstract BaseType getFieldType();

    protected boolean isByteOrIllegalSlot(Record record) {
        final FrameSlotKind kind = record.getFrameDescriptor().getFrameSlotKind(getIdentifier());
        return kind == FrameSlotKind.Byte || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isByteOrIllegalSlot(frame)")
    protected void writeByte(Record record, byte value) {
        record.putObject(getIdentifier(), value);
    }

    protected boolean isIntOrIllegalSlot(Record record) {
        final FrameSlotKind kind = record.getFrameDescriptor().getFrameSlotKind(getIdentifier());
        return kind == FrameSlotKind.Int || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isIntOrIllegalSlot(frame)")
    protected void writeInt(Record record, int value) {
        record.putObject(getIdentifier(), value);
    }

    protected boolean isLongOrIllegalSlot(Record record) {
        return kind == FrameSlotKind.Long || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isLongOrIllegalSlot(frame)")
    protected void writeLong(Record record, long value) {
        record.putObject(getIdentifier(), value);
    }

    protected boolean isFloatOrIllegalSlot(Record record) {
        final FrameSlotKind kind = record.getFrameDescriptor().getFrameSlotKind(getIdentifier());
        return kind == FrameSlotKind.Float || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isFloatOrIllegalSlot(frame)")
    protected void writeFloat(Record record, float value) {
        record.putObject(getIdentifier(), value);
    }

    protected boolean isDoubleOrIllegalSlot(Record record) {
        return kind == FrameSlotKind.Double || kind == FrameSlotKind.Illegal;
    }

    @Specialization(guards = "isDoubleOrIllegalSlot(frame)")
    protected void writeDouble(Record record, double value) {
        record.putObject(getIdentifier(), value);
    }

    @Specialization(replaces = { "writeByte", "writeInt", "writeLong", "writeFloat", "writeDouble" })
    protected void write(Record record, Object value) {
        record.putObject(getIdentifier(), value);
    }

    public abstract void executeWrite(Record record, Object value);

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }

}
