package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.ExpressionNode;

/**
 * Node representing the root node of a function's AST.
 */
public class FunctionI4GLRootNode extends I4GLRootNode {

    public FunctionI4GLRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, ExpressionNode bodyNode) {
        super(language, frameDescriptor, bodyNode);
    }
}
