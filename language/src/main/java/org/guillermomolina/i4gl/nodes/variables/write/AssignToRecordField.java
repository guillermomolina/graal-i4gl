package org.guillermomolina.i4gl.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.RecordValue;

/**
 * Node representing assignment to a record. Compared to {@link SimpleAssignmentNode} it assigns the value to the record's
 * frame instead of function's frame.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AssignToRecordFieldNodeGen}
 */
@NodeChildren({
        @NodeChild(value = "recordNode", type = ExpressionNode.class),
        @NodeChild(value = "valueNode", type = ExpressionNode.class)
})
public abstract class AssignToRecordField extends StatementNode {

    private final String identifier;

    AssignToRecordField(String identifier) {
        this.identifier = identifier;
    }

    @Specialization
    void assignInt(RecordValue record, int value) {
        record.getFrame().setInt(record.getSlot(this.identifier), value);
    }

    @Specialization
    void assignLong(RecordValue record, long value) {
        record.getFrame().setLong(record.getSlot(this.identifier), value);
    }

    @Specialization
    void assignDouble(RecordValue record, double value) {
        record.getFrame().setDouble(record.getSlot(this.identifier), value);
    }

    @Specialization
    void assignChar(RecordValue record, char value) {
        record.getFrame().setByte(record.getSlot(this.identifier), (byte) value);
    }

    @Specialization
    void assignGeneric(RecordValue record, Object value) {
        record.getFrame().setObject(record.getSlot(this.identifier), value);
    }

}
