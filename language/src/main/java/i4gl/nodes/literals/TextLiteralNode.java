package i4gl.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;

/**
 * Node representing string literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link TextLiteralNodeGen}
 */
public final class TextLiteralNode extends ExpressionNode {

	private final String value;

	public TextLiteralNode(String value) {
		this.value = value;
	}

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

}