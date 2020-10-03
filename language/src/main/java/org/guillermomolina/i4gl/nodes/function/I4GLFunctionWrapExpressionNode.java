package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

public class I4GLFunctionWrapExpressionNode extends I4GLExpressionNode {
    @Child
    private I4GLStatementNode functionNode;

    public I4GLFunctionWrapExpressionNode(I4GLStatementNode functionNode) {
        this.functionNode = functionNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        functionNode.executeVoid(frame);
        return null;
    }

    @Override
    public I4GLTypeDescriptor getType() {
        return null;
    }    
}