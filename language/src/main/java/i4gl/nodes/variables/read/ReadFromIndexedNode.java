package i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.exceptions.I4GLRuntimeException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Array;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Varchar;

/**
 * This nodes read value from an array at specified index.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ReadFromArrayNodeGen}
 */

@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeChild(value = "indexNode", type = ExpressionNode.class)
@NodeField(name = "returnType", type = BaseType.class)
public abstract class ReadFromIndexedNode extends ExpressionNode {

    protected abstract BaseType getReturnType();

    @Specialization
    String readText(String string, int index) {
        return Character.toString((string.charAt(index - 1)));
    }

    @Specialization
    String readChar(Char charValue, int index) {
        return Character.toString(charValue.getCharAt(index - 1));
    }

    @Specialization
    String readVarchar(Varchar varchar, int index) {
        return Character.toString(varchar.getCharAt(index - 1));
    }

    @Specialization
    Object readArray(Array array, int index) {
        try {
            return array.getValueAt(index - 1);
        } catch (InvalidArrayIndexException e) {
            throw new I4GLRuntimeException(e.getMessage());
        }
    }

    @Override
    public BaseType getType() {
        return this.getReturnType();
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
