package i4gl.nodes.statement;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.UnexpectedRuntimeException;
import i4gl.nodes.cast.CastToTextNodeGen;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.values.Null;

@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
public class DisplayNode extends StatementNode {
    @Child
    private ExpressionNode argumentNode;

    public DisplayNode(final ExpressionNode argumentNode) {
        this.argumentNode = argumentNode;
    }

    private void display(final String text) {
        Context.get(this).getOutput().println(text);
    }

    @Override
    public void executeVoid(final VirtualFrame frame) {
        BaseType argumentNodeType = argumentNode.getReturnType();
        ExpressionNode valueNode;
        if (argumentNodeType == TextType.SINGLETON) {
            valueNode = argumentNode;
        } else {
            valueNode = CastToTextNodeGen.create(argumentNode);
        }
        Object value = valueNode.executeGeneric(frame);
        if (value == Null.SINGLETON) {
            display(argumentNodeType.getNullString());
        } else {
            if(!(value instanceof String)) {
                throw new UnexpectedRuntimeException();
            }
            display((String)value);
        }
    }

}
