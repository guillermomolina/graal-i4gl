package i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;

import i4gl.exceptions.I4GLRuntimeException;
import i4gl.exceptions.NotImplementedException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.values.Array;

/**
 * Node representing assignment to an array. Compared to
 * {@link AssignToLocalVariableNode} it has to receive also index.
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle. {@link AssignToArrayNodeGen}
 */
@NodeChild(value = "arrayNode", type = ExpressionNode.class)
@NodeChild(value = "indexNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
public abstract class AssignToIndexedNode extends StatementNode {
    
    @Specialization
    void assignArray(Array array, int index, Object value) {
        try {
            array.setValueAt(index - 1, value);
        } catch (InvalidArrayIndexException e) {
            throw new I4GLRuntimeException(e.getMessage());
        }
    }
    
    @Specialization
    void assignArray(Object array, int index, Object value) {
        throw new NotImplementedException("Should not be here");
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}
