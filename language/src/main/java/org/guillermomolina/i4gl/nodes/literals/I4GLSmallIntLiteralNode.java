package org.guillermomolina.i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallIntType;

/**
 * Node representing short literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link SmallIntLiteralNodeGen}
 */
public abstract class I4GLSmallIntLiteralNode extends I4GLExpressionNode {

	private final short value;

	I4GLSmallIntLiteralNode(short value) {
		this.value = value;
	}

	@Specialization
	public short execute() {
		return value;
	}

    @Override
    public I4GLType getType() {
        return I4GLSmallIntType.SINGLETON;
    }

}