package org.guillermomolina.i4gl.nodes.variables.read;

import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.Tag;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;

public class I4GLReadFromReturnNode extends I4GLExpressionNode {

    @Override
    Object executeGeneric(Object[] array, int index) {
        return array[index - 1];
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == ReadVariableTag.class || super.hasTag(tag);
    }

}
