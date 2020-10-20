package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.I4GLBinaryExpressionNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;

/**
 * Node representing logical or operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link OrNodeGen}
 */
@NodeInfo(shortName = "OR")
public abstract class I4GLOrNode extends I4GLBinaryExpressionNode {
    @Specialization
    int or(int left, int right) {
        return (left != 0 || right != 0) ? 1 : 0;
    }

	@Specialization
	int or(long left, long right) {
		return (left != 0 || right != 0) ? 1 : 0;
	}

	@Specialization
	int or(float left, float right) {
		return (left != '0' || right != '0') ? 1 : 0;
	}

	@Specialization
	int or(double left, double right) {
		return (left != 0 || right != 0) ? 1 : 0;
	}

    @Override
    public I4GLType getType() {
        return I4GLIntType.SINGLETON;
    }

}
