package i4gl.nodes.variables.read;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "type", type = BaseType.class)
public abstract class ReadLocalVariableNode extends ExpressionNode {

    protected abstract FrameSlot getSlot();
    protected abstract BaseType getType();

    public BaseType getReturnType() {
        return getType();
    }

    @Specialization(guards = "returnsChar()")
    protected char readChar(final VirtualFrame frame) {
        return (char)FrameUtil.getByteSafe(frame, getSlot());
    }

    protected boolean returnsSmallIntAndIsShortSlot(VirtualFrame frame) {
        Object result = frame.getValue(getSlot());
        return returnsSmallInt() && result instanceof Short;
    }

    @Specialization(guards = "returnsSmallIntAndIsShortSlot(frame)")
    protected short readSmallInt(final VirtualFrame frame) {
        Short value = (Short)FrameUtil.getObjectSafe(frame, getSlot());
        return value.shortValue();
    }

    @Specialization(guards = "returnsInt()")
    protected int readInt(final VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, getSlot());
    }

    @Specialization(guards = "returnsBigInt()")
    protected long readBigInt(final VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, getSlot());
    }

    @Specialization(guards = "returnsSmallFloat()")
    protected float readSmallFloat(final VirtualFrame frame) {
        return FrameUtil.getFloatSafe(frame, getSlot());
    }

    @Specialization(guards = "returnsFloat()")
    protected double readFloat(final VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, getSlot());
    }

    @Specialization(replaces = { "readChar", "readInt", "readBigInt", "readSmallFloat", "readFloat" })
    protected Object readObject(final VirtualFrame frame) {
        Object result = frame.getValue(getSlot());
        if(result == null) {
            CompilerDirectives.transferToInterpreter();
            result = getReturnType().getDefaultValue();
            frame.setObject(getSlot(), result);
            return result;
        }

        return FrameUtil.getObjectSafe(frame, getSlot());
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
