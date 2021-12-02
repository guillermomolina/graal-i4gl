package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = ",")
public abstract class I4GLConcatenationNode extends I4GLBinaryExpressionNode {
    @Specialization
    protected String concat(String left, String right) {
        return left + right;
    }

    @Specialization
    protected String concat(String left, Object right) {
        if (right == null) {
            return left;
        }
        return left + right.toString();
    }
}
