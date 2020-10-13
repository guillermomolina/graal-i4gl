package org.guillermomolina.i4gl.nodes.variables.write;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.I4GLTypeSystem;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLCharType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLVarcharType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLBigIntType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLFloatType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallFloatType;
import org.guillermomolina.i4gl.runtime.values.I4GLChar;
import org.guillermomolina.i4gl.runtime.values.I4GLRecord;
import org.guillermomolina.i4gl.runtime.values.I4GLVarchar;

/**
 * Node representing assignment to a variable of primitive type.
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle. {@link SimpleAssignmentNodeGen}
 */
@NodeFields({ @NodeField(name = "slot", type = FrameSlot.class),
        @NodeField(name = "type", type = I4GLType.class), })
@NodeChild(value = "valueNode", type = I4GLExpressionNode.class)
@TypeSystemReference(I4GLTypeSystem.class)
public abstract class I4GLSimpleAssignmentNode extends I4GLStatementNode {

    public abstract FrameSlot getSlot();

    protected abstract I4GLType getType();

    @CompilerDirectives.CompilationFinal
    private int jumps = -1;

    protected boolean isInt() {
        return getType() == I4GLIntType.SINGLETON;
    }

    @Specialization(guards = "isInt()")
    void writeInt(final VirtualFrame frame, final int value) {
        getFrame(frame).setInt(getSlot(), value);
    }
    
    protected boolean isBigInt() {
        return getType() == I4GLBigIntType.SINGLETON;
    }

    @Specialization(guards = "isBigInt()")
    void writeBigInt(final VirtualFrame frame, final long value) {
        getFrame(frame).setLong(getSlot(), value);
    }

    protected boolean isSmallFloat() {
        return getType() == I4GLSmallFloatType.SINGLETON;
    }

    @Specialization(guards = "isSmallFloat()")
    void writeSmallFloat(final VirtualFrame frame, final float value) {
        getFrame(frame).setFloat(getSlot(), value);
    }

    protected boolean isDouble() {
        return getType() == I4GLFloatType.SINGLETON;
    }

    @Specialization(guards = "isDouble()")
    void writeDouble(final VirtualFrame frame, final double value) {
        getFrame(frame).setDouble(getSlot(), value);
    }

    protected boolean isChar() {
        return getType() instanceof I4GLCharType;
    }

    @Specialization(guards = "isChar()")
    void assignChar(final VirtualFrame frame, final String string) {
        I4GLChar value = (I4GLChar) getType().getDefaultValue();
        value.assignString(string);
        getFrame(frame).setObject(getSlot(), value);
    }

    protected boolean isVarchar() {
        return getType() instanceof I4GLVarcharType;
    }

    @Specialization(guards = "isVarchar()")
    void assignVarchar(final VirtualFrame frame, final String string) {
        I4GLVarchar value = (I4GLVarchar) getType().getDefaultValue();
        value.assignString(string);
        getFrame(frame).setObject(getSlot(), value);
    }

    @Specialization
    void assignRecord(final VirtualFrame frame, final I4GLRecord record) {
        getFrame(frame).setObject(getSlot(), record.createDeepCopy());
    }

    @Specialization
    void assignIntArray(final VirtualFrame frame, final int[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignBigIntArray(final VirtualFrame frame, final long[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignSmallFloatArray(final VirtualFrame frame, final float[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignDoubleArray(final VirtualFrame frame, final double[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    /**
     * This is used for multidimensional arrays
     */
    @Specialization
    void assignArray(final VirtualFrame frame, final Object[] array) {
        getFrame(frame).setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization(replaces = {"writeInt", "writeBigInt", "writeSmallFloat", "writeDouble", "assignChar", "assignVarchar", "assignRecord", "assignIntArray", "assignBigIntArray", "assignSmallFloatArray", "assignDoubleArray", "assignArray"})
    void assign(final VirtualFrame frame, final Object value) {
        getFrame(frame).setObject(getSlot(), value);
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

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}
