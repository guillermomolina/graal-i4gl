package org.guillermomolina.i4gl.nodes.arithmetic;

import com.oracle.truffle.api.dsl.NodeChild;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Base class for each unary node. It has one child node which is the operation's argument.
 */
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class UnaryNode extends ExpressionNode {

    protected abstract ExpressionNode getArgument();

    /**
     * Results of the most unary types have the same type as its argument (e.g. -1, not, ...)
     * @return resulting type of the operation
     */
    @Override
    public TypeDescriptor getType() {
        return this.getArgument().getType();
    }

}
