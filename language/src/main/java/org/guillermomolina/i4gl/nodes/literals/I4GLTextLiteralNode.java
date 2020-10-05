package org.guillermomolina.i4gl.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;

/**
 * Node representing string literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link TextLiteralNodeGen}
 */
public final class I4GLTextLiteralNode extends I4GLExpressionNode {

	private final String value;

	public I4GLTextLiteralNode(String value) {
		this.value = value;
	}

    @Override
    public String executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override
    public I4GLTypeDescriptor getType() {
        return TextDescriptor.SINGLETON;
    }

}