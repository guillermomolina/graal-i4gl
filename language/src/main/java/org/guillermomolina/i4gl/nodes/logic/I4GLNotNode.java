package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.expression.I4GLUnaryNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;

/**
 * Node representing logical not operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link NotNodeGen}
 */
@NodeInfo(shortName = "!")
public abstract class I4GLNotNode extends I4GLUnaryNode {

	@Override
	public abstract int executeInt(VirtualFrame frame);

	@Specialization
	int logicalNot(short child) {
		return child == 0 ? 1 : 0;
	}
	
	@Specialization
	int logicalNot(int child) {
		return child == 0 ? 1 : 0;
	}

	@Specialization
	int logicalNot(long child) {
		return child == 0 ? 1 : 0;
	}

    @Override
    public I4GLType getType() {
        return I4GLIntType.SINGLETON;
    }
}
