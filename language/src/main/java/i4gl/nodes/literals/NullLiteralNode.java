package i4gl.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.NullType;
import i4gl.runtime.values.Null;


public class NullLiteralNode extends ExpressionNode {

    @Override
 	public Object executeGeneric(VirtualFrame frame) {
 		return Null.SINGLETON;
	}

	@Override
	public BaseType getReturnType() {
		return NullType.SINGLETON;
	}
}
