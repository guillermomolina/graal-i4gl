package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.I4GLTypes;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.runtime.exceptions.HaltException;

/**
 * This node represents the root node of AST of any function or main program.
 */
@TypeSystemReference(I4GLTypes.class)
public class I4GLRootNode extends RootNode {

	@Child
	protected ExpressionNode bodyNode;

	public I4GLRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, ExpressionNode bodyNode) {
		super(language, frameDescriptor);
		this.bodyNode = bodyNode;
	}

    @Override
    public Object execute(VirtualFrame virtualFrame) {
        try {
            return bodyNode.executeGeneric(virtualFrame);
        } catch (HaltException e) {
            return e.getExitCode();
        }
    }    

}
