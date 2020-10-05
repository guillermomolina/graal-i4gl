package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.I4GLBinaryExpressionNode;

/**
 * Node representing logical and operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AndNodeGen}
 */
@NodeInfo(shortName = "and")
public abstract class I4GLAndNode extends I4GLBinaryExpressionNode {
    @Specialization
    int and(int left, int right) {
        return (left != 0 && right != 0) ? 1 : 0;
    }

	@Specialization
	int and(long left, long right) {
		return (left != 0 && right != 0) ? 1 : 0;
	}

	@Specialization
	int and(float left, float right) {
		return (left != '0' && right != '0') ? 1 : 0;
	}

	@Specialization
	int and(double left, double right) {
		return (left != 0 && right != 0) ? 1 : 0;
	}
}