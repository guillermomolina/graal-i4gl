package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.RecordValue;
import org.guillermomolina.i4gl.runtime.exceptions.UnexpectedRuntimeException;

/**
 * This node reads value from an record with specified identifier.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ReadFromRecordNodeGen}
 */
@NodeChild(value = "record", type = ExpressionNode.class)
@NodeFields({
    @NodeField(name = "identifier", type = String.class),
    @NodeField(name = "returnType", type = TypeDescriptor.class)
})
public abstract class ReadFromRecordNode extends ExpressionNode {

    protected abstract TypeDescriptor getReturnType();

    protected abstract String getIdentifier();

    @Specialization(guards = "isInt()")
    int readInt(RecordValue record) {
        FrameSlot slot = record.getSlot(this.getIdentifier());
        try {
            return record.getFrame().getInt(slot);
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization(guards = "isLong()")
    long readLong(RecordValue record) {
        FrameSlot slot = record.getSlot(this.getIdentifier());
        try {
            return record.getFrame().getLong(slot);
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization(guards = "isFloat()")
    float readFloat(RecordValue record) {
        FrameSlot slot = record.getSlot(this.getIdentifier());
        try {
            return record.getFrame().getFloat(slot);
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization(guards = "isDouble()")
    double readDouble(RecordValue record) {
        FrameSlot slot = record.getSlot(this.getIdentifier());
        try {
            return record.getFrame().getDouble(slot);
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedRuntimeException();
        }
    }

    @Specialization
    Object readGeneric(RecordValue record) {
        FrameSlot slot = record.getSlot(this.getIdentifier());
        return record.getFrame().getValue(slot);
    }

    @Override
    public TypeDescriptor getType() {
        return this.getReturnType();
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }
}
