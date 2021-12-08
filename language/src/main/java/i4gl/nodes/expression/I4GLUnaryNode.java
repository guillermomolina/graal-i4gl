package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.NodeChild;

import i4gl.runtime.types.BaseType;

/**
 * Base class for each unary node. It has one child node which is the operation's argument.
 */
@NodeChild(value = "argument", type = I4GLExpressionNode.class)
public abstract class I4GLUnaryNode extends I4GLExpressionNode {

    protected abstract I4GLExpressionNode getArgument();

    /**
     * Results of the most unary types have the same type as its argument (e.g. -1, not, ...)
     * @return resulting type of the operation
     */
    @Override
    public BaseType getType() {
        return this.getArgument().getType();
    }

}
