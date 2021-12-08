package i4gl.nodes.function;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;

import i4gl.interop.MainFunctionObject;
import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.root.I4GLMainRootNode;
import i4gl.runtime.types.BaseType;

@NodeField(name = "slot", type = FrameSlot.class)
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
    public BaseType getType() {
        return null;
    }

}
