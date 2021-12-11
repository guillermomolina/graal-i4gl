package i4gl.nodes.variables.read;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.BaseType;

@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "type", type = BaseType.class)
@NodeField(name = "frameName", type = String.class)
public abstract class ReadNonLocalVariableNode extends ExpressionNode {
    @CompilationFinal
    protected VirtualFrame globalFrame;

    protected abstract FrameSlot getSlot();
    protected abstract BaseType getType();
    protected abstract String getFrameName();    

    public BaseType getReturnType() {
        return getType();
    }

    protected VirtualFrame getFrame() {
        if(globalFrame == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            globalFrame = Context.get(this).getModuleFrame(getFrameName());
        }
        return globalFrame;
    }

    @Specialization(guards = "returnsChar()")
    protected char readChar(final VirtualFrame frame) {
        return (char)FrameUtil.getByteSafe(getFrame(), getSlot());
    }

    protected boolean returnsSmallIntAndIsShortSlot() {
        Object result = getFrame().getValue(getSlot());
        return returnsSmallInt() && result instanceof Short;
    }

    @Specialization(guards = "returnsSmallIntAndIsShortSlot()")
    protected short readSmallInt(final VirtualFrame frame) {
        Short value = (Short)FrameUtil.getObjectSafe(getFrame(), getSlot());
        return value.shortValue();
    }

    @Specialization(guards = "returnsInt()")
    protected int readInt(final VirtualFrame frame) {
        return FrameUtil.getIntSafe(getFrame(), getSlot());
    }

    @Specialization(guards = "returnsBigInt()")
    protected long readBigInt(final VirtualFrame frame) {
        return FrameUtil.getLongSafe(getFrame(), getSlot());
    }

    @Specialization(guards = "returnsSmallFloat()")
    protected float readSmallFloat(final VirtualFrame frame) {
        return FrameUtil.getFloatSafe(getFrame(), getSlot());
    }

    @Specialization(guards = "returnsFloat()")
    protected double readFloat(final VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(getFrame(), getSlot());
    }

    @Specialization(replaces = { "readChar", "readInt", "readBigInt", "readSmallFloat", "readFloat" })
    protected Object readObject(final VirtualFrame frame) {
        Object result = getFrame().getValue(getSlot());
        if(result == null) {
            CompilerDirectives.transferToInterpreter();
            result = getReturnType().getDefaultValue();
            getFrame().setObject(getSlot(), result);
            return result;
        }

        return FrameUtil.getObjectSafe(getFrame(), getSlot());
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
