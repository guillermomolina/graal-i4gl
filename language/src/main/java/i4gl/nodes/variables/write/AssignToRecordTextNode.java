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
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Record;
import i4gl.runtime.values.Varchar;


@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
@NodeChild(value = "recordNode", type = ExpressionNode.class)
@NodeChild(value = "indexNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
public abstract class AssignToRecordTextNode extends StatementNode {

    protected abstract String getIdentifier();
    protected abstract BaseType getFieldType();

    protected boolean isChar() {
        return getFieldType() instanceof CharType;
    }

    @Specialization(guards = "isChar()")
    void assignChar(final Record record, final int index, final String value) {
        Object targetObject = record.get(getIdentifier());
        if (!(targetObject instanceof Char)) {
            targetObject = getFieldType().getDefaultValue();
            record.put(getIdentifier(), targetObject);
        }

        final Char target = (Char) targetObject;
        target.setCharAt(index - 1, value.charAt(0));
    }

    protected boolean isVarchar() {
        return getFieldType() instanceof VarcharType;
    }

    @Specialization(guards = "isVarchar()")
    void assignVarchar(final Record record, final int index, final String value) {
        Object targetObject = record.get(getIdentifier());
        if (!(targetObject instanceof Varchar)) {
            targetObject = getFieldType().getDefaultValue();
            record.put(getIdentifier(), targetObject);
        }

        final Varchar target = (Varchar) targetObject;
        target.setCharAt(index - 1, value.charAt(0));
    }

    protected boolean isText() {
        return getFieldType() instanceof TextType;
    }

    @Specialization(guards = "isText()")
    void assignText(final Record record, final int index, final String value) {
        Object targetObject = record.get(getIdentifier());
        if (!(targetObject instanceof String)) {
            targetObject = "";
        }
        StringBuilder builder = new StringBuilder((String) targetObject);
        builder.setCharAt(index, value.charAt(0));
        record.put(getIdentifier(), targetObject);
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}
