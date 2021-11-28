package i4gl.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.runtime.types.I4GLType;

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
    public I4GLType getType() {
        return null;
    }    
}