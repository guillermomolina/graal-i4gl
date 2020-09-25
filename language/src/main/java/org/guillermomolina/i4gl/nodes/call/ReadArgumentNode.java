package org.guillermomolina.i4gl.nodes.call;

import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;

/**
 * This node reads one argument from actual frame at specified index. It is used mainly with assignment node where this
 * node reads value of received argument and the assignment node assigns it to the variable representing the argument.
 *
 * {@link org.guillermomolina.i4gl.parser.NodeFactory#addParameterIdentifiersToLexicalScope(List)} ()}
 */
public class ReadArgumentNode extends ExpressionNode {

	private final int index;
	private final TypeDescriptor argumentType;

	public ReadArgumentNode(int index, TypeDescriptor argumentType) {
		this.index = index + 1;
        this.argumentType = argumentType;
    }

	@Override
	public Object executeGeneric(VirtualFrame frame) {
        return frame.getArguments()[index];
	}

    @Override
    public TypeDescriptor getType() {
        return this.argumentType;
    }

}
