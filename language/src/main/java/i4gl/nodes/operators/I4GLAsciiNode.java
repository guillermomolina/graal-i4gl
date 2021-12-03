package i4gl.nodes.operators;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.I4GLUnaryNode;

@NodeInfo(shortName = "ASCII")
public abstract class I4GLAsciiNode extends I4GLUnaryNode {

	@Specialization
	String ascii(int argument) {
		return Character.toString ((char) argument);
	}
}
