package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.runtime.values.Null;

@NodeInfo(shortName = ",")
public abstract class ConcatenationNode extends BinaryExpressionNode {
    @Specialization
    protected String concat(final String left, final String right) {
        return left + right;
    }

    @Specialization
    protected String concat(final Object left, final Object right) {
        String value;
        if(left == Null.SINGLETON) {
            value = getLeftNode().getReturnType().getNullString();
        } else {
            value = left.toString();
        }
        if (right == Null.SINGLETON) {
            value += getRightNode().getReturnType().getNullString();
        } else {
            value += right.toString();
        }
        return value;
    }
}
