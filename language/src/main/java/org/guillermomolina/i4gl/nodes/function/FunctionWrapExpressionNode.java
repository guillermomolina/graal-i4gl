package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;

public class FunctionWrapExpressionNode extends ExpressionNode {
    @Child
    private StatementNode functionNode;

    public FunctionWrapExpressionNode(StatementNode functionNode) {
        this.functionNode = functionNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        functionNode.executeVoid(frame);
        return null;
    }

    @Override
    public TypeDescriptor getType() {
        return null;
    }    
}