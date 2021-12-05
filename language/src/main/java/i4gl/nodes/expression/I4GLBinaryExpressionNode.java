package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.NodeChild;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.I4GLType;

/**
 * Base node for each binary expression node. It also contains functions for
 * static type checking.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class I4GLBinaryExpressionNode extends I4GLExpressionNode {

    /**
     * Gets the left argument node.
     */
    protected abstract I4GLExpressionNode getLeftNode();

    /**
     * Gets the right argument node.
     */
    protected abstract I4GLExpressionNode getRightNode();

    @Override
    public I4GLType getType() {
        I4GLType leftNodeType = getLeftNode().getType();
        I4GLType rightNodeType = getRightNode().getType();
        if (leftNodeType == rightNodeType) {
            return leftNodeType;
        }
        if (leftNodeType.convertibleTo(rightNodeType)) {
            return rightNodeType;
        }
        if (rightNodeType.convertibleTo(leftNodeType)) {
            return leftNodeType;
        }
        throw new NotImplementedException();
    }

}
