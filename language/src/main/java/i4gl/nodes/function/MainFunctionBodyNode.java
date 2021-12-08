package i4gl.nodes.function;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;

import i4gl.interop.MainFunctionObject;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.root.MainRootNode;
import i4gl.runtime.types.BaseType;

@NodeField(name = "slot", type = FrameSlot.class)
public abstract class MainFunctionBodyNode extends ExpressionNode {

	private final MainRootNode rootNode;

	MainFunctionBodyNode(MainRootNode rootNode) {
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
