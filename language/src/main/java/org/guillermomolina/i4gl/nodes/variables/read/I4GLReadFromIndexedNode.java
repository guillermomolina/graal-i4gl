package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.CharValue;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * This nodes read value from an array at specified index.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ReadFromArrayNodeGen}
 */
@NodeChildren({
        @NodeChild(value = "valueNode", type = I4GLExpressionNode.class),
        @NodeChild(value = "indexNode", type = I4GLExpressionNode.class)
})
@NodeField(name = "returnType", type = I4GLTypeDescriptor.class)
public abstract class I4GLReadFromIndexedNode extends I4GLExpressionNode {

    protected abstract I4GLTypeDescriptor getReturnType();

    @Specialization
    int readInt(int[] array, int index) {
        return array[index - 1];
    }

    @Specialization
    long readBigInt(long[] array, int index) {
        return array[index - 1];
    }

    @Specialization
    float readSmallFloat(float[] array, int index) {
        return array[index - 1];
    }

    @Specialization
    double readDouble(double[] array, int index) {
        return array[index - 1];
    }

    @Specialization
    String readText(String string, int index) {
        return Character.toString((string.charAt(index - 1)));
    }

    @Specialization
    String readChar(CharValue charValue, int index) {
        return Character.toString(charValue.getCharAt(index - 1));
    }

    @Specialization
    String readVarchar(VarcharValue varchar, int index) {
        return Character.toString(varchar.getCharAt(index - 1));
    }

    @Specialization
    Object readGeneric(Object[] array, int index) {
        return array[index - 1];
    }

    @Override
    public I4GLTypeDescriptor getType() {
        return this.getReturnType();
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
