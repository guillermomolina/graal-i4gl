package i4gl.nodes.variables.write;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.Record;


@NodeField(name = "identifier", type = String.class)
@NodeField(name = "fieldType", type = BaseType.class)
@NodeChild(value = "recordNode", type = ExpressionNode.class)
@NodeChild(value = "valueNode", type = ExpressionNode.class)
public abstract class AssignToRecordFieldNode extends StatementNode {

    protected abstract String getIdentifier();
    protected abstract BaseType getFieldType();

    protected boolean isSmallInt() {
        return getFieldType() == SmallIntType.SINGLETON;
    }

    @Specialization(guards = "isSmallInt()")
    void assignSmallInt(final Record record, final short value) {
        record.put(getIdentifier(), value);
    }

    @Specialization(guards = "isSmallInt()")
    void assignSmallInt(final Record record, final int value) {
        record.put(getIdentifier(), value);
    }

    protected boolean isInt() {
        return getFieldType() == IntType.SINGLETON;
    }

    @Specialization(guards = "isInt()")
    @TruffleBoundary
    void assignInt(final Record record, final int value) {
        record.put(getIdentifier(), value);
    }

    protected boolean isBigInt() {
        return getFieldType() == BigIntType.SINGLETON;
    }

    @Specialization(guards = "isBigInt()")
    @TruffleBoundary
    void assignBigInt(final Record record, final long value) {
        record.put(getIdentifier(), value);
    }

    protected boolean isSmallFloat() {
        return getFieldType() == SmallFloatType.SINGLETON;
    }

    @Specialization(guards = "isSmallFloat()")
    void assignSmallFloat(final Record record, final float value) {
        record.put(getIdentifier(), value);
    }

    protected boolean isFloat() {
        return getFieldType() == FloatType.SINGLETON;
    }

    @Specialization(guards = "isFloat()")
    @TruffleBoundary
    void assignDouble(final Record record, final double value) {
        record.put(getIdentifier(), value);
    }

    @Specialization
    void assignGeneric(final Record record, final Null value) {
        record.put(getIdentifier(), value);
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == WriteVariableTag.class || super.hasTag(tag);
    }
}
