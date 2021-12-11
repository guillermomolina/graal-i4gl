package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.NodeChild;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;

/**
 * Base node for each binary expression node. It also contains functions for
 * static type checking.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class BinaryExpressionNode extends ExpressionNode {

    /**
     * Gets the left argument node.
     */
    protected abstract ExpressionNode getLeftNode();

    /**
     * Gets the right argument node.
     */
    protected abstract ExpressionNode getRightNode();

    @Override
    public BaseType getReturnType() {
        BaseType leftNodeType = getLeftNode().getReturnType();
        BaseType rightNodeType = getRightNode().getReturnType();
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
