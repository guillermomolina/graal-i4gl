package i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.NotImplementedException;
import i4gl.nodes.expression.I4GLBinaryExpressionNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.primitive.I4GLIntType;

/**
 * Node representing like operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link LikeNodeGen}
 */
@NodeInfo(shortName = "LIKE")
public abstract class I4GLLikeNode extends I4GLBinaryExpressionNode {

    I4GLLikeNode() {
        throw new NotImplementedException();
    }

    @Specialization
    protected int like(int left, int right) {
        return left == right ? 1 : 0;
    }

    @Override
    public I4GLType getType() {
        return I4GLIntType.SINGLETON;
    }
}