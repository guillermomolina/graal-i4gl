package i4gl.nodes.operators;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.common.NumberFormatter;
import i4gl.exceptions.NumberFormatterOverflowException;
import i4gl.nodes.expression.BinaryExpressionNode;
import i4gl.runtime.values.Null;

@NodeInfo(shortName = "USING")
public abstract class UsingNode extends BinaryExpressionNode {

	@Specialization
	protected Object using(short value, String format) {
		try {
			return NumberFormatter.Format(format, value);
		} catch (NumberFormatterOverflowException e) {
			return Null.SINGLETON;
		}
	}

	@Specialization
	@TruffleBoundary
	protected Object using(int value, String format) {
		try {
			return NumberFormatter.Format(format, value);
		} catch (NumberFormatterOverflowException e) {
			return Null.SINGLETON;
		}
	}

	@Specialization
	@TruffleBoundary
	protected Object using(long value, String format) {
		try {
			return NumberFormatter.Format(format, value);
		} catch (NumberFormatterOverflowException e) {
			return Null.SINGLETON;
		}
	}

	@Specialization
	protected Object using(float value, String format) {
		return NumberFormatter.Format(format, value);
	}

	@Specialization
	@TruffleBoundary
	protected Object using(double value, String format) {
		return NumberFormatter.Format(format, value);
	}
}
