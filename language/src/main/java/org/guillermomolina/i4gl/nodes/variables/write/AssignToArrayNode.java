package org.guillermomolina.i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.nodes.variables.ReadIndexNode;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;

/**
 * Node representing assignment to an array. Compared to {@link SimpleAssignmentNode} it has to receive also index.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AssignToArrayNodeGen}
 */
@NodeChildren({
        @NodeChild(value = "arrayNode", type = ExpressionNode.class),
        @NodeChild(value = "indexNode", type = ReadIndexNode.class),
        @NodeChild(value = "valueNode", type = ExpressionNode.class)
})
public abstract class AssignToArrayNode extends StatementNode {

    @Specialization
    void assignInt(int[] array, int index, int value) {
        array[index] = value;
    }

    @Specialization
    void assignLong(long[] array, int index, long value) {
        array[index] = value;
    }

    @Specialization
    void assignDouble(double[] array, int index, double value) {
        array[index] = value;
    }

    @Specialization
    void assignChar(char[] array, int index, char value) {
        array[index] = value;
    }

    @Specialization
    void assignToString(I4GLString string, int index, char value) {
        string.setValueAt(index, value);
    }

    @Specialization
    void assignToVarchar(VarcharValue string, int index, char value) {
        string.setValueAt(index, value);
    }

    @Specialization
    void assignObject(Object[] array, int index, Object value) {
        array[index] = value;
    }

}
