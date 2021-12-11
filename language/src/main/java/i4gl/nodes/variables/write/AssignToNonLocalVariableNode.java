package i4gl.nodes.variables.write;

import java.text.ParseException;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.ArrayType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.RecordType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import i4gl.runtime.values.Array;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Date;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.Record;
import i4gl.runtime.values.Varchar;

/**
 * Node representing assignment to a variable of primitive type.
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle. {@link SimpleAssignmentNodeGen}
 */
@NodeField(name = "slot", type = FrameSlot.class)
@NodeField(name = "type", type = BaseType.class)
@NodeField(name = "frameName", type = String.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
public abstract class AssignToNonLocalVariableNode extends StatementNode {

    protected abstract FrameSlot getSlot();
    public abstract BaseType getType();
    protected abstract String getFrameName();    
 
    @CompilationFinal
    protected VirtualFrame globalFrame;
    
    protected boolean isSmallInt() {
        return getType() == SmallIntType.SINGLETON;
    }

    @Specialization(guards = "isSmallInt()")
    void writeSmallInt(final VirtualFrame frame, final short value) {
        getGlobalFrame().setObject(getSlot(), value);
    }

    @Specialization(guards = "isSmallInt()")
    void writeSmallInt(final VirtualFrame frame, final int value) {
        getGlobalFrame().setObject(getSlot(), value);
    }

    protected boolean isInt() {
        return getType() == IntType.SINGLETON;
    }

    @Specialization(guards = "isInt()")
    void writeInt(final VirtualFrame frame, final int value) {
        getGlobalFrame().setInt(getSlot(), value);
    }
    
    protected boolean isBigInt() {
        return getType() == BigIntType.SINGLETON;
    }

    @Specialization(guards = "isBigInt()")
    void writeBigInt(final VirtualFrame frame, final long value) {
        getGlobalFrame().setLong(getSlot(), value);
    }

    protected boolean isSmallFloat() {
        return getType() == SmallFloatType.SINGLETON;
    }

    @Specialization(guards = "isSmallFloat()")
    void writeSmallFloat(final VirtualFrame frame, final float value) {
        getGlobalFrame().setFloat(getSlot(), value);
    }

    protected boolean isFloat() {
        return getType() == FloatType.SINGLETON;
    }

    @Specialization(guards = "isFloat()")
    void writeDouble(final VirtualFrame frame, final double value) {
        getGlobalFrame().setDouble(getSlot(), value);
    }

    protected boolean isText() {
        return getType() == TextType.SINGLETON;
    }

    @Specialization(guards = "isText()")
    void assignText(final VirtualFrame frame, final String string) {
        getGlobalFrame().setObject(getSlot(), string);
    }

    protected boolean isChar() {
        return getType() instanceof CharType;
    }

    @Specialization(guards = "isChar()")
    void assignChar(final VirtualFrame frame, final String string) {
        Char value = (Char) getType().getDefaultValue();
        value.assignString(string);
        getGlobalFrame().setObject(getSlot(), value);
    }

    @Specialization(guards = "isChar()")
    void assignChar(final VirtualFrame frame, final Char value) {
        getGlobalFrame().setObject(getSlot(), value.createDeepCopy());
    }

    protected boolean isVarchar() {
        return getType() instanceof VarcharType;
    }

    @Specialization(guards = "isVarchar()")
    void assignVarchar(final VirtualFrame frame, final String string) {
        Varchar value = (Varchar) getType().getDefaultValue();
        value.assignString(string);
        getGlobalFrame().setObject(getSlot(), value);
    }

    @Specialization(guards = "isVarchar()")
    void assignVarchar(final VirtualFrame frame, final Varchar value) {
        getGlobalFrame().setObject(getSlot(), value.createDeepCopy());
    }

    protected boolean isDate() {
        return getType() == DateType.SINGLETON;
    }

    @Specialization(guards = "isDate()")
    void assignDate(final VirtualFrame frame, final int value) {
        getGlobalFrame().setObject(getSlot(), Date.valueOf(value));
    }

    @Specialization(guards = "isDate()")
    void assignDate(final VirtualFrame frame, final String value) {
        try {
            getGlobalFrame().setObject(getSlot(), Date.valueOf(value));
        } catch (ParseException e) {
            getGlobalFrame().setObject(getSlot(), Null.SINGLETON);
        }
    }

    @Specialization(guards = "isDate()")
    void assignDate(final VirtualFrame frame, final Date value) {
        getGlobalFrame().setObject(getSlot(), value);
    }


    protected boolean isRecord() {
        return getType() instanceof RecordType;
    }

    @Specialization(guards = "isRecord()")
    void assignRecord(final VirtualFrame frame, final Record record) {
        getGlobalFrame().setObject(getSlot(), record.createDeepCopy());
    }

    protected boolean isArray() {
        return getType() instanceof ArrayType;
    }

    @Specialization(guards = "isArray()")
    void assignArray(final VirtualFrame frame, final Array array) {
        getGlobalFrame().setObject(getSlot(), array.createDeepCopy());
    }

    @Specialization
    void assign(final VirtualFrame frame, final Null value) {
        getGlobalFrame().setObject(getSlot(), value);
    }

    protected VirtualFrame getGlobalFrame() {
        if(globalFrame == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            globalFrame = Context.get(this).getModuleFrame(getFrameName());
        }
        return globalFrame;
    }

    @Override
    public boolean hasTag(final Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}
