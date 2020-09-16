package org.guillermomolina.i4gl.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.StringDescriptor;

/**
 * Node representing string literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link StringLiteralNodeGen}
 */
public abstract class StringLiteralNode extends ExpressionNode {

	private final I4GLString value;

	StringLiteralNode(String value) {
		this.value = new I4GLString(value);
	}

	@Specialization
	public I4GLString execute() {
		return value;
	}

    @Override
    public TypeDescriptor getType() {
        return StringDescriptor.getInstance();
    }

}
