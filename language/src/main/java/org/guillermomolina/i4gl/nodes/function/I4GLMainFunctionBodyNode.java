package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.interop.MainFunctionObject;
import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.root.I4GLMainRootNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

@NodeFields({
    @NodeField(name = "slot", type = FrameSlot.class),
})
public abstract class I4GLMainFunctionBodyNode extends I4GLExpressionNode {

	private final I4GLMainRootNode rootNode;

	I4GLMainFunctionBodyNode(I4GLMainRootNode rootNode) {
        this.rootNode = rootNode;
    }

	@Specialization
    Object execute() {
	    return MainFunctionObject.writeVariable(rootNode, rootNode.getSourceSection());
    }

    @Override
    public I4GLTypeDescriptor getType() {
        return null;
    }

}
