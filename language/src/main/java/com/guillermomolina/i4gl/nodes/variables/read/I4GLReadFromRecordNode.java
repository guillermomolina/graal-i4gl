package com.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.values.I4GLRecord;

/**
 * This node reads value from an record with specified identifier.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ReadFromRecordNodeGen}
 */
@NodeChild(value = "record", type = I4GLExpressionNode.class)
@NodeField(name = "identifier", type = String.class)
@NodeField(name = "returnType", type = I4GLType.class)
public abstract class I4GLReadFromRecordNode extends I4GLExpressionNode {

    protected abstract I4GLType getReturnType();

    protected abstract String getIdentifier();

    @Specialization(guards = "record.isSmallInt(getIdentifier())")
    short readSmallInt(I4GLRecord record) {
        return record.getSmallIntSafe(getIdentifier());
    }

    @Specialization(guards = "record.isInt(getIdentifier())")
    int readInt(I4GLRecord record) {
        return record.getIntSafe(getIdentifier());
    }

    @Specialization(guards = "record.isBigInt(getIdentifier())")
    long readBigInt(I4GLRecord record) {
        return record.getBigIntSafe(getIdentifier());
    }

    @Specialization(guards = "record.isSmallFloat(getIdentifier())")
    float readSmallFloat(I4GLRecord record) {
        return (float)record.getSmallFloatSafe(getIdentifier());
    }

    @Specialization(guards = "record.isFloat(getIdentifier())")
    double readFloat(I4GLRecord record) {
        return (double)record.getFloatSafe(getIdentifier());
    }

    @Specialization(replaces = { "readInt", "readBigInt", "readSmallFloat", "readFloat" })
    Object readGeneric(I4GLRecord record) {
        return record.get(getIdentifier());
    }

    @Override
    public I4GLType getType() {
        return this.getReturnType();
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }
}
