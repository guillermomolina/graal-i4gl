package i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Record;

@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
@NodeChild(value = "record", type = ExpressionNode.class)
public abstract class ReadFromRecordNode extends ExpressionNode {

    protected abstract BaseType getFieldType();
    protected abstract String getIdentifier();

    @Override
    public BaseType getReturnType() {
        return this.getFieldType();
    }

    @Specialization(guards = "returnsChar()")
    protected char readChar(final Record record) {
        return (char)record.getCharSafe(getIdentifier());
    }

    @Specialization(guards = "returnsSmallInt()")
    protected short readSmallInt(final Record record) {
        Short value = record.getSmallIntSafe(getIdentifier());
        return value.shortValue();
    }

    @Specialization(guards = "returnsInt()")
    protected int readInt(final Record record) {
        return record.getIntSafe(getIdentifier());
    }

    @Specialization(guards = "returnsBigInt()")
    protected long readBigInt(final Record record) {
        return record.getBigIntSafe(getIdentifier());
    }

    @Specialization(guards = "returnsSmallFloat()")
    protected float readSmallFloat(final Record record) {
        return record.getSmallFloatSafe(getIdentifier());
    }

    @Specialization(guards = "returnsFloat()")
    protected double readFloat(final Record record) {
        return record.getFloatSafe(getIdentifier());
    }

    @Specialization(replaces = { "readChar", "readInt", "readBigInt", "readSmallFloat", "readFloat" })
    protected Object readObject(final Record record) {
        return record.getObject(getIdentifier());
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
