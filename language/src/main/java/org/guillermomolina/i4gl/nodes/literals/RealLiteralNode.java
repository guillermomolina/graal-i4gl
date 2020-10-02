package org.guillermomolina.i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.FloatDescriptor;

/**
 * Node representing real literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link RealLiteralNode}
 */
public abstract class RealLiteralNode extends ExpressionNode {

	private final double value;

	RealLiteralNode(double value) {
		this.value = value;
	}

	@Specialization
	public double execute() {
		return value;
	}

    @Override
    public TypeDescriptor getType() {
        return FloatDescriptor.SINGLETON;
    }

}
