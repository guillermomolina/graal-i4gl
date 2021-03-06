package com.guillermomolina.i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLFloatType;

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
    public I4GLType getType() {
        return I4GLFloatType.SINGLETON;
    }

}
