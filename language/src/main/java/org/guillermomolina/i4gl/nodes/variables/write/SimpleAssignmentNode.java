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
import org.guillermomolina.i4gl.runtime.customvalues.I4GLSubroutine;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLArray;
import org.guillermomolina.i4gl.runtime.customvalues.*;
import org.guillermomolina.i4gl.parser.identifierstable.types.extension.PCharDesriptor;

import java.util.Arrays;

/**
 * Node representing assignment to a variable of primitive type.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link SimpleAssignmentNodeGen}
 */
@NodeField(name = "slot", type = FrameSlot.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
public abstract class SimpleAssignmentNode extends StatementNode {

    protected abstract FrameSlot getSlot();

    @CompilerDirectives.CompilationFinal private int jumps = -1;

    @Specialization
    void writeInt(VirtualFrame frame, int value) {
        getFrame(frame).setInt(getSlot(), value);
    }

    @Specialization
    void writeLong(VirtualFrame frame, long value) {
        getFrame(frame).setLong(getSlot(), value);
    }

    @Specialization
    void writeBoolean(VirtualFrame frame, boolean value) {
        getFrame(frame).setBoolean(getSlot(), value);
    }

    @Specialization
    void writeChar(VirtualFrame frame, char value) {
        getFrame(frame).setByte(getSlot(), (byte) value);
    }

    @Specialization
    void writeDouble(VirtualFrame frame, double value) {
        getFrame(frame).setDouble(getSlot(), value);
    }

    @Specialization
    void writeEnum(VirtualFrame frame, EnumValue value) {
        getFrame(frame).setObject(getSlot(), value);
    }

    @Specialization
    void assignSet(VirtualFrame frame, SetTypeValue set) {
        getFrame(frame).setObject(getSlot(), set.createDeepCopy());
    }

    @Specialization
    void assignRecord(VirtualFrame frame, RecordValue record) {
        getFrame(frame).setObject(getSlot(), record.getCopy());
    }

    @Specialization
    void assignPointers(VirtualFrame frame, PointerValue pointer) {
        PointerValue assignmentTarget = (PointerValue) getFrame(frame).getValue(getSlot());
        assignmentTarget.setHeapSlot((pointer).getHeapSlot());
    }

    @Specialization
    void assignString(VirtualFrame frame, I4GLString value) {
        frame = getFrame(frame);
        Object targetObject = frame.getValue(getSlot());
        if (targetObject instanceof I4GLString) {
            frame.setObject(getSlot(), value);
        } else if (targetObject instanceof PointerValue) {
            PointerValue pointerValue = (PointerValue) targetObject;
            if (pointerValue.getType() instanceof PCharDesriptor) {
                assignPChar(pointerValue, value);
            }
        }
    }

    @Specialization
    void assignSubroutine(VirtualFrame frame, I4GLSubroutine subroutine) {
        getFrame(frame).setObject(getSlot(), subroutine);
    }

    @Specialization
    void assignIntArray(VirtualFrame frame, int[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignLongArray(VirtualFrame frame, long[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignDoubleArray(VirtualFrame frame, double[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignCharArray(VirtualFrame frame, char[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignBooleanArray(VirtualFrame frame, boolean[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    /**
     * This is used for multidimensional arrays
     */
    @Specialization
    void assignArray(VirtualFrame frame, Object[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }


    @Specialization
    void assignArray(VirtualFrame frame, I4GLArray array) {
        getFrame(frame).setObject(getSlot(), array.createDeepCopy());
    }

    private void assignPChar(PointerValue pcharPointer, I4GLString value) {
        PCharValue pchar = (PCharValue) pcharPointer.getDereferenceValue();
        pchar.assignString(value.toString());
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
