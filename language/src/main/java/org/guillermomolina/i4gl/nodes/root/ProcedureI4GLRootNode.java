package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.function.ProcedureWrapExpressionNode;

/**
 * This node represents a root node for each I4GL procedure. Since procedures do not return any value
 * they are represented by a statement node. This statement node therefore needs to be wrapped into an
 * expression node to be passed to the parent in c'tor.
 */
public class ProcedureI4GLRootNode extends I4GLRootNode {

    public ProcedureI4GLRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, StatementNode bodyNode) {
        super(language, frameDescriptor, new ProcedureWrapExpressionNode(bodyNode));
    }

}
