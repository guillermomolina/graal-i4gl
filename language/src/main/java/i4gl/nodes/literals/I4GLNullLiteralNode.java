package i4gl.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.primitive.I4GLNullType;
import i4gl.runtime.values.I4GLNull;

/**
 * Node representing null literal.
 *
 */
public class I4GLNullLiteralNode extends I4GLExpressionNode {
    @Override
	public Object executeGeneric(VirtualFrame frame) {
		return I4GLNull.SINGLETON;
	}

    @Override
    public I4GLType getType() {
        return I4GLNullType.SINGLETON;
    }

}
