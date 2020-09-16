package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.function.ProcedureWrapExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.HaltException;

/**
 * Node representing the root node of I4GL's main function's AST.
 */
public class MainFunctionI4GLRootNode extends I4GLRootNode {

    public MainFunctionI4GLRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, StatementNode bodyNode) {
        super(language, frameDescriptor, new ProcedureWrapExpressionNode(bodyNode));
    }

    @Override
    public Object execute(VirtualFrame virtualFrame) {
        try {
            bodyNode.executeGeneric(virtualFrame);
        } catch (HaltException e) {
            return e.getExitCode();
        }

        return 0;
    }

}
