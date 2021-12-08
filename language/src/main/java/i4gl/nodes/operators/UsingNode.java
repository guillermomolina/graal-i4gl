package i4gl.nodes.operators;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.common.NumberFormatter;
import i4gl.exceptions.NotImplementedException;
import i4gl.nodes.expression.BinaryExpressionNode;

@NodeInfo(shortName = "USING")
public abstract class UsingNode extends BinaryExpressionNode {

	@Specialization(rewriteOn = ArithmeticException.class)
	protected String using(int value, String format) {
		return NumberFormatter.Format(format, value);
	}

	@Specialization
	@TruffleBoundary
	protected String using(long value, String format) {
		return NumberFormatter.Format(format, value);
	}

	@Specialization
	protected String using(float value, String format) {
		return NumberFormatter.Format(format, value);
	}

	@Specialization
	@TruffleBoundary
	protected String using(double value, String format) {
		return NumberFormatter.Format(format, value);
	}

	@Specialization
	protected String using(Object value, String format) {
		throw new NotImplementedException("using(Object left, String format)");
	}
}
