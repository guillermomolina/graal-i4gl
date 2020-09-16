package org.guillermomolina.i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.LongDescriptor;

/**
 * Node representing long literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link LongLiteralNodeGen}
 */
public abstract class LongLiteralNode extends ExpressionNode {

	private final long value;

	LongLiteralNode(long value) {
		this.value = value;
	}

	@Specialization
	public long execute() {
		return value;
	}

    @Override
    public TypeDescriptor getType() {
        return LongDescriptor.getInstance();
    }

}
