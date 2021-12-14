package i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.UndefinedNameException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.types.BaseType;

@NodeChild(value = "recordNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
public abstract class WriteRecordFieldNode extends StatementNode {

    static final int LIBRARY_LIMIT = 3;

    protected abstract String getIdentifier();

    // TODO: Check correct type at runtime
    protected abstract BaseType getFieldType();

    @Specialization(limit = "LIBRARY_LIMIT")
    protected void writeObject(Object record, Object value,
            @CachedLibrary("record") InteropLibrary objectLibrary) {
        try {
            objectLibrary.writeMember(record, getIdentifier(), value);
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException e) {
            throw UndefinedNameException.undefinedProperty(this, getIdentifier());
        }
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }

}
