package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.arithmetic.I4GLUnaryNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.NullValue;

/**
 * Node representing logical not operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link IsNullNodeGen}
 */
@NodeInfo(shortName = "IS NULL")
public abstract class I4GLIsNullNode extends I4GLUnaryNode {

	@Override
	public abstract int executeInt(VirtualFrame frame);

	@Specialization
	int isNull(Object child) {
		return child == NullValue.SINGLETON ? 1 : 0;
	}

    @Override
    public I4GLTypeDescriptor getType() {
        return IntDescriptor.SINGLETON;
    }
}