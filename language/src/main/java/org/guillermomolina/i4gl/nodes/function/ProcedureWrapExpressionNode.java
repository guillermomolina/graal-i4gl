package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Our {@link org.guillermomolina.i4gl.nodes.root.I4GLRootNode} needs to be provided with an {@link ExpressionNode}
 * so we cannot pass it {@link ProcedureBodyNode} directly. We encapsulate the procedure's body inside this node which
 * only executes it and returns null.
 */
public class ProcedureWrapExpressionNode extends ExpressionNode {

    @Child
    private StatementNode procedureNode;

    public ProcedureWrapExpressionNode(StatementNode procedureNode) {
        this.procedureNode = procedureNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        procedureNode.executeVoid(frame);
        return null;
    }

    @Override
    public TypeDescriptor getType() {
        return null;
    }
}
