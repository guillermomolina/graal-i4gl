package i4gl.nodes.statement;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.values.Null;

@NodeInfo(shortName = "DISPLAY", description = "The node implementing the DISPLAY statement")
@NodeChild(value = "argumentNode", type = ExpressionNode.class)
public abstract class DisplayNode extends StatementNode {

    protected abstract ExpressionNode getArgumentNode();

    @Specialization
    public void display(final String argument) {
        Context.get(this).getOutput().println(argument);
    }

    @Specialization
    public void display(final Object argument) {
        String value;
        if (argument == Null.SINGLETON) {
            value = getArgumentNode().getReturnType().getNullString();
        } else {
            value = argument.toString();
        }
        Context.get(this).getOutput().println(value);
    }
}
