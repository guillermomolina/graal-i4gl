package i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.FloatType;

/**
 * Node representing real literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link I4GLDoubleLiteralNode}
 */
public abstract class I4GLDoubleLiteralNode extends I4GLExpressionNode {

	private final double value;

	I4GLDoubleLiteralNode(double value) {
		this.value = value;
	}

	@Specialization
	public double execute() {
		return value;
	}

    @Override
    public BaseType getType() {
        return FloatType.SINGLETON;
    }

}
