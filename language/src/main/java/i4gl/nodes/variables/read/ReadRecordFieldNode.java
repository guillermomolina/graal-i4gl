package i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.UndefinedNameException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;

@NodeChild(value = "record", type = ExpressionNode.class)
@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
public abstract class ReadRecordFieldNode extends ExpressionNode {
    static final int LIBRARY_LIMIT = 3;

    protected abstract String getIdentifier();

    // TODO: Check correct type at runtime
    protected abstract BaseType getFieldType();

    @Override
    public BaseType getReturnType() {
        return getFieldType();
    }

    @Specialization(guards = "objects.hasMembers(record)", limit = "LIBRARY_LIMIT")
    protected Object readObject(Object record,
            @CachedLibrary("record") InteropLibrary objects) {
        try {
            return objects.readMember(record, getIdentifier());
        } catch (UnsupportedMessageException | UnknownIdentifierException e) {
            throw UndefinedNameException.undefinedProperty(this, getIdentifier());
        }
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
