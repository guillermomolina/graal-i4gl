package org.guillermomolina.i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.runtime.values.I4GLChar;
import org.guillermomolina.i4gl.runtime.values.I4GLVarchar;

@NodeInfo(shortName = ",")
public abstract class I4GLConcatenationNode extends I4GLBinaryExpressionNode {
    @Specialization
    protected String concat(String left, I4GLVarchar right) {
        return left + right.toString();
    }

    @Specialization
    protected String concat(String left, I4GLChar right) {
        return left + right.toString();
    }

    @Specialization
    protected String concat(String left, String right) {
        return left + right;
    }
}
