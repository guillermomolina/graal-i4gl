package i4gl.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.UnexpectedRuntimeException;
import i4gl.nodes.cast.CastToTextNodeGen;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.values.Null;

@NodeInfo(shortName = ",")
public class ConcatenationNode extends ExpressionNode {
    @Child
    private ExpressionNode leftNode;
    @Child
    private ExpressionNode rightNode;

    public ConcatenationNode(final ExpressionNode leftNode, final ExpressionNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

    public String executeNode(final VirtualFrame frame, final ExpressionNode node) {
        BaseType nodeType = node.getReturnType();
        ExpressionNode valueNode;
        if (nodeType == TextType.SINGLETON) {
            valueNode = node;
        } else {
            valueNode = CastToTextNodeGen.create(node);
        }
        Object value = valueNode.executeGeneric(frame);
        if (value == Null.SINGLETON) {
            return nodeType.getNullString();
        }
        if (!(value instanceof String)) {
            throw new UnexpectedRuntimeException();
        }
        return (String) value;
    }

    @Override
    public Object executeGeneric(final VirtualFrame frame) {
        final String leftValue = executeNode(frame, leftNode);
        final String rightValue = executeNode(frame, rightNode);
        return leftValue + rightValue;
    }
}
