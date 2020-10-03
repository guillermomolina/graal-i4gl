package org.guillermomolina.i4gl.nodes.call;

import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

/**
 * This node reads one argument from actual frame at specified index. It is used mainly with assignment node where this
 * node reads value of received argument and the assignment node assigns it to the variable representing the argument.
 *
 * {@link org.guillermomolina.i4gl.parser.NodeFactory#addParameterIdentifiersToLexicalScope(List)} ()}
 */
public class I4GLReadArgumentNode extends I4GLExpressionNode {

	private final int index;
	private final I4GLTypeDescriptor argumentType;

	public I4GLReadArgumentNode(int index, I4GLTypeDescriptor argumentType) {
		this.index = index + 1;
        this.argumentType = argumentType;
    }

	@Override
	public Object executeGeneric(VirtualFrame frame) {
        return frame.getArguments()[index];
	}

    @Override
    public I4GLTypeDescriptor getType() {
        return this.argumentType;
    }

}
