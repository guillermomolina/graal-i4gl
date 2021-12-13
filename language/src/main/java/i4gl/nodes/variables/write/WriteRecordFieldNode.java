package i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Record;
import i4gl.runtime.values.Varchar;

@NodeChild(value = "recordNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
public abstract class WriteRecordFieldNode extends StatementNode {

    protected abstract String getIdentifier();

    // TODO: Check correct type at runtime
    protected abstract BaseType getFieldType();

    /*
     * @Specialization(guards = "record.isChar(getIdentifier())")
     * protected void writeChar1(Record record, char value) {
     * throw new UnexpectedRuntimeException();
     * // record.setChar(getIdentifier(), value);
     * }
     */
    @Specialization(guards = "record.isSmallInt(getIdentifier())")
    protected void writeSmallInt(Record record, short value) {
        record.setSmallInt(getIdentifier(), value);
    }

    @Specialization(guards = "record.isInt(getIdentifier())")
    protected void writeInt(Record record, int value) {
        record.setInt(getIdentifier(), value);
    }

    @Specialization(guards = "record.isBigInt(getIdentifier())")
    protected void writeBigInt(Record record, long value) {
        record.setBigInt(getIdentifier(), value);
    }

    @Specialization(guards = "record.isSmallFloat(getIdentifier())")
    protected void writeSmallFloat(Record record, float value) {
        record.setSmallFloat(getIdentifier(), value);
    }

    @Specialization(guards = "record.isFloat(getIdentifier())")
    protected void writeFloat(Record record, double value) {
        record.setFloat(getIdentifier(), value);
    }

    protected boolean isChar(Record record) {
        return record.getRecordType().getFieldType(getIdentifier()) instanceof CharType;
    }

    @Specialization(guards = "isChar(record)")
    protected void writeChar(Record record, String string) {
        Char value = (Char) getFieldType().getDefaultValue();
        value.assignString(string);
        record.setObject(getIdentifier(), value);
    }

    protected boolean isVarchar(Record record) {
        return record.getRecordType().getFieldType(getIdentifier()) instanceof VarcharType;
    }

    @Specialization(guards = "isVarchar(record)")
    protected void writeVarchar(Record record, String string) {
        Varchar value = (Varchar) getFieldType().getDefaultValue();
        value.assignString(string);
        record.setObject(getIdentifier(), value);
    }

    @Specialization(replaces = { /* "writeChar1", */"writeSmallInt", "writeInt", "writeBigInt", "writeSmallFloat",
            "writeFloat" })
    protected void write(Record record, Object value) {
        record.setObject(getIdentifier(), value);
    }

    public abstract void executeWrite(Record record, Object value);

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }

}
