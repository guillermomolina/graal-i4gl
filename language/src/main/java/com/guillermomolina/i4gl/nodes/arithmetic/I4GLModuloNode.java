package com.guillermomolina.i4gl.nodes.arithmetic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.guillermomolina.i4gl.nodes.expression.I4GLBinaryExpressionNode;

/**
 * Node representing modulo operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link I4GLModuloNodeGen}
 */
@NodeInfo(shortName = "MOD")
public abstract class I4GLModuloNode extends I4GLBinaryExpressionNode {

    @Specialization
    protected int mod(int left, int right) {
        return left % right;
    }

	@Specialization
	protected long mod(long left, long right) {
		return left % right;
	}
}
