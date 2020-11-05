package org.guillermomolina.i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.runtime.values.I4GLChar;
import org.guillermomolina.i4gl.runtime.values.I4GLVarchar;

@NodeInfo(shortName = "CLIPPED")
public abstract class I4GLClippedNode extends I4GLUnaryNode {

	@Specialization
	I4GLVarchar clip(I4GLVarchar argument) {
		return argument;
	}

	@Specialization
	I4GLChar clip(I4GLChar argument) {
		return argument;
	}

	@Specialization
	String clip(String argument) {
		return argument;
	}
}
