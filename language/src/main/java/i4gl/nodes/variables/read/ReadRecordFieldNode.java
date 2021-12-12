package i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.exceptions.UnexpectedRuntimeException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Record;

@NodeChild(value = "record", type = ExpressionNode.class)
@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
public abstract class ReadRecordFieldNode extends ExpressionNode {

    protected abstract String getIdentifier();

    // TODO: Check correct type at runtime
    protected abstract BaseType getFieldType();

    @Override
    public BaseType getReturnType() {
        return getFieldType();
    }

    @Specialization(guards = "record.isChar(getIdentifier())")
    protected char readChar1(final Record record) {
        throw new UnexpectedRuntimeException();
        // return record.getCharSafe(getIdentifier());
    }

    @Specialization(guards = "record.isSmallInt(getIdentifier())")
    protected short readSmallInt(final Record record) {
        return record.getSmallIntSafe(getIdentifier());
    }

    @Specialization(guards = "record.isInt(getIdentifier())")
    protected int readInt(final Record record) {
        return record.getIntSafe(getIdentifier());
    }

    @Specialization(guards = "record.isBigInt(getIdentifier())")
    protected long readBigInt(final Record record) {
        return record.getBigIntSafe(getIdentifier());
    }

    @Specialization(guards = "record.isSmallFloat(getIdentifier())")
    protected float readSmallFloat(final Record record) {
        return record.getSmallFloatSafe(getIdentifier());
    }

    @Specialization(guards = "record.isFloat(getIdentifier())")
    protected double readFloat(final Record record) {
        return record.getFloatSafe(getIdentifier());
    }

    @Specialization(replaces = { "readChar1", "readSmallInt", "readInt", "readBigInt", "readSmallFloat", "readFloat" })
    protected Object readObject(final Record record) {
        return record.getObject(getIdentifier());
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
