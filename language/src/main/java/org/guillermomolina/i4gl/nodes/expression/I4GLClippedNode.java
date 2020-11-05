package org.guillermomolina.i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.runtime.values.I4GLChar;

@NodeInfo(shortName = "CLIPPED")
public abstract class I4GLClippedNode extends I4GLUnaryNode {
	@Specialization
	Object clip(I4GLChar argument) {
		return argument.clipped();
	}

	@Specialization
	Object clip(Object argument) {
		return argument;
	}
}
