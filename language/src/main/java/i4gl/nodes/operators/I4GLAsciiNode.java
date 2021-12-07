package i4gl.nodes.operators;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.I4GLLanguage;
import i4gl.nodes.expression.I4GLUnaryNode;

@NodeInfo(shortName = "ASCII")
public abstract class I4GLAsciiNode extends I4GLUnaryNode {
    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(I4GLAsciiNode.class);

	@Specialization
	char ascii(int argument) {
		if(argument < 0 || argument > 255) {
			LOGGER.warning("Argument " + argument + " is not in char range");
		}
		return (char)argument;
	}
}
