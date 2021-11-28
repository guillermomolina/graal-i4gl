package i4gl.nodes.variables.write;

import java.util.Arrays;

import i4gl.nodes.I4GLTypeSystem;
import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLCharType;
import i4gl.runtime.types.compound.I4GLVarcharType;
import i4gl.runtime.types.primitive.I4GLBigIntType;
import i4gl.runtime.types.primitive.I4GLFloatType;
import i4gl.runtime.types.primitive.I4GLIntType;
import i4gl.runtime.types.primitive.I4GLSmallFloatType;
import i4gl.runtime.types.primitive.I4GLSmallIntType;
import i4gl.runtime.values.I4GLChar;
import i4gl.runtime.values.I4GLRecord;
import i4gl.runtime.values.I4GLVarchar;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

/**
 * Node representing assignment to a variable of primitive type.
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle. {@link SimpleAssignmentNodeGen}
 */
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "type", type = I4GLType.class)
@NodeField(name = "frameName", type = String.class)
@NodeChild(value = "valueNode", type = I4GLExpressionNode.class)
@TypeSystemReference(I4GLTypeSystem.class)
public abstract class I4GLAssignToNonLocalVariableNode extends I4GLStatementNode {

    public abstract String getFrameName();

    public abstract FrameSlot getSlot();

    protected abstract I4GLType getType();

    @CompilationFinal
    protected VirtualFrame globalFrame;
    
    protected boolean isSmallInt() {
        return getType() == I4GLSmallIntType.SINGLETON;
    }

    @Specialization(guards = "isSmallInt()")
    void writeSmallInt(final VirtualFrame frame, final short value) {
        getGlobalFrame().setObject(getSlot(), value);
    }

    protected boolean isInt() {
        return getType() == I4GLIntType.SINGLETON;
    }

    @Specialization(guards = "isInt()")
    void writeInt(final VirtualFrame frame, final int value) {
        getGlobalFrame().setInt(getSlot(), value);
    }
    
    protected boolean isBigInt() {
        return getType() == I4GLBigIntType.SINGLETON;
    }

    @Specialization(guards = "isBigInt()")
    void writeBigInt(final VirtualFrame frame, final long value) {
        getGlobalFrame().setLong(getSlot(), value);
    }

    protected boolean isSmallFloat() {
        return getType() == I4GLSmallFloatType.SINGLETON;
    }

    @Specialization(guards = "isSmallFloat()")
    void writeSmallFloat(final VirtualFrame frame, final float value) {
        getGlobalFrame().setFloat(getSlot(), value);
    }

    protected boolean isFloat() {
        return getType() == I4GLFloatType.SINGLETON;
    }

    @Specialization(guards = "isFloat()")
    void writeDouble(final VirtualFrame frame, final double value) {
        getGlobalFrame().setDouble(getSlot(), value);
    }

    protected boolean isChar() {
        return getType() instanceof I4GLCharType;
    }

    @Specialization(guards = "isChar()")
    void assignChar(final VirtualFrame frame, final String string) {
        I4GLChar value = (I4GLChar) getType().getDefaultValue();
        value.assignString(string);
        getGlobalFrame().setObject(getSlot(), value);
    }

    protected boolean isVarchar() {
        return getType() instanceof I4GLVarcharType;
    }

    @Specialization(guards = "isVarchar()")
    void assignVarchar(final VirtualFrame frame, final String string) {
        I4GLVarchar value = (I4GLVarchar) getType().getDefaultValue();
        value.assignString(string);
        getGlobalFrame().setObject(getSlot(), value);
    }

    @Specialization
    void assignRecord(final VirtualFrame frame, final I4GLRecord record) {
        getGlobalFrame().setObject(getSlot(), record.createDeepCopy());
    }

    @Specialization
    void assignIntArray(final VirtualFrame frame, final int[] array) {
        getGlobalFrame().setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignSmallIntArray(final VirtualFrame frame, final short[] array) {
        getGlobalFrame().setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignBigIntArray(final VirtualFrame frame, final long[] array) {
        getGlobalFrame().setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignSmallFloatArray(final VirtualFrame frame, final float[] array) {
        getGlobalFrame().setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization
    void assignDoubleArray(final VirtualFrame frame, final double[] array) {
        getGlobalFrame().setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    /**
     * This is used for multidimensional arrays
     */
    @Specialization
    void assignArray(final VirtualFrame frame, final Object[] array) {
        getGlobalFrame().setObject(getSlot(), Arrays.copyOf(array, array.length));
    }

    @Specialization(replaces = {"writeSmallInt", "writeInt", "writeBigInt", "writeSmallFloat", "writeDouble", "assignChar", "assignVarchar", "assignRecord", "assignSmallIntArray", "assignIntArray", "assignBigIntArray", "assignSmallFloatArray", "assignDoubleArray", "assignArray"})
    void assign(final VirtualFrame frame, final Object value) {
        getGlobalFrame().setObject(getSlot(), value);
    }

    protected VirtualFrame getGlobalFrame() {
        if(globalFrame == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            globalFrame = I4GLContext.get(this).getModuleFrame(getFrameName());
        }
        return globalFrame;
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}