package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.interop.MainFunctionObject;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.root.MainFunctionRootNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;

@NodeFields({
    @NodeField(name = "slot", type = FrameSlot.class),
})
public abstract class MainFunctionBodyNode extends ExpressionNode {

	private final MainFunctionRootNode rootNode;

	MainFunctionBodyNode(MainFunctionRootNode rootNode) {
        this.rootNode = rootNode;
    }

	@Specialization
    Object execute() {
	    return MainFunctionObject.writeVariable(rootNode, rootNode.getSourceSection());
    }

    @Override
    public TypeDescriptor getType() {
        return null;
    }

}
