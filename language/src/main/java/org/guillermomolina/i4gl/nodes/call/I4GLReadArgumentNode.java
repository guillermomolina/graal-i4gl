package org.guillermomolina.i4gl.nodes.call;

import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;

/**
 * This node reads one argument from actual frame at specified index. It is used mainly with assignment node where this
 * node reads value of received argument and the assignment node assigns it to the variable representing the argument.
 *
 * {@link org.guillermomolina.i4gl.parser.I4GLNodeFactory#addParameterIdentifiersToLexicalScope(List)} ()}
 */
public class I4GLReadArgumentNode extends I4GLExpressionNode {

	private final int index;
	private final I4GLType argumentType;

	public I4GLReadArgumentNode(int index, I4GLType argumentType) {
		this.index = index;
        this.argumentType = argumentType;
    }

	@Override
	public Object executeGeneric(VirtualFrame frame) {
        return frame.getArguments()[index];
	}

    @Override
    public I4GLType getType() {
        return this.argumentType;
    }

}
