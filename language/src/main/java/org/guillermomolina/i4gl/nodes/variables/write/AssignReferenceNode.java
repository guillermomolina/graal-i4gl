package org.guillermomolina.i4gl.nodes.variables.write;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.*;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLArray;

/**
 * Node representing assignment to a reference type variable. Compared to {@link SimpleAssignmentNode} it has to firstly
 * unpack the reference.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AssignReferenceNodeGen}
 */
@NodeChild(value = "valueNode", type = ExpressionNode.class)
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class AssignReferenceNode extends StatementNode {

    protected abstract FrameSlot getSlot();

    @CompilerDirectives.CompilationFinal private int jumps = -1;

    @Specialization
    void writeInt(VirtualFrame frame, int value) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        reference.getFromFrame().setInt(reference.getFrameSlot(), value);
    }

    @Specialization
    void writeLong(VirtualFrame frame, long value) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        reference.getFromFrame().setLong(reference.getFrameSlot(), value);
    }

    @Specialization
    void writeChar(VirtualFrame frame, char value) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        reference.getFromFrame().setByte(reference.getFrameSlot(), (byte) value);
    }

    @Specialization
    void writeDouble(VirtualFrame frame, double value) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        reference.getFromFrame().setDouble(reference.getFrameSlot(), value);
    }

    @Specialization
    void assignRecord(VirtualFrame frame, RecordValue record) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        reference.getFromFrame().setObject(reference.getFrameSlot(), record.getCopy());
    }

    @Specialization
    void assignPointer(VirtualFrame frame, PointerValue pointer) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        PointerValue assignmentTarget = (PointerValue) reference.getFromFrame().getValue(reference.getFrameSlot());
        assignmentTarget.setHeapSlot(pointer.getHeapSlot());
    }

    @Specialization
    void assignString(VirtualFrame frame, I4GLString value) {
        frame = getFrame(frame);
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        Object targetObject = reference.getFromFrame().getValue(reference.getFrameSlot());
        if (targetObject instanceof I4GLString) {
            reference.getFromFrame().setObject(reference.getFrameSlot(), value);
        } else if (targetObject instanceof PointerValue) {
            PointerValue pointerValue = (PointerValue) targetObject;
            if (pointerValue.getType() instanceof VarcharDescriptor) {
                assignVarchar(pointerValue, value);
            }
        }
    }

    @Specialization
    void assignArray(VirtualFrame frame, I4GLArray array) {
        Reference reference = (Reference) getFrame(frame).getValue(getSlot());
        reference.getFromFrame().setObject(reference.getFrameSlot(), array.createDeepCopy());
    }

    private void assignVarchar(PointerValue varcharPointer, I4GLString value) {
        VarcharValue varchar = (VarcharValue) varcharPointer.getDereferenceValue();
        varchar.assignString(value.toString());
    }

    @ExplodeLoop
    private VirtualFrame getFrame(VirtualFrame frame) {
        if (jumps == -1) {
            jumps = this.getJumpsToFrame(frame, getSlot());
            CompilerDirectives.transferToInterpreterAndInvalidate();
        }

        for (int i = 0; i < jumps; ++i) {
            frame = (VirtualFrame) frame.getArguments()[0];
        }

        return frame;
    }

}
