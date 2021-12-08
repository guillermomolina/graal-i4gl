package i4gl.nodes.operators;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.values.I4GLChar;

@NodeInfo(shortName = "CLIPPED")
public abstract class ClippedNode extends UnaryNode {
	@Specialization
	Object clip(I4GLChar argument) {
		return argument.clipped();
	}

	@Specialization
	Object clip(Object argument) {
		return argument;
	}
}
