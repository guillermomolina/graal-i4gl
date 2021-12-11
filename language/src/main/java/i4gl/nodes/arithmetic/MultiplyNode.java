package i4gl.nodes.arithmetic;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.exceptions.I4GLRuntimeException;
import i4gl.nodes.expression.BinaryExpressionNode;

/**
 * Node representing I4GL's multiplication operation. If the arguments are sets, then it is understood as intersection.
 *
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link MultiplyNodeGen}
 */
@NodeInfo(shortName = "*")
public abstract class MultiplyNode extends BinaryExpressionNode {

    @Specialization(rewriteOn = ArithmeticException.class)
    int mul(int left, int right) {
        return Math.multiplyExact(left, right);
    }

    @Specialization
    @TruffleBoundary
    long mul(long left, long right) {
        return Math.multiplyExact(left, right);
    }

	@Specialization
	float mul(float left, float right) {
		return left * right;
	}

    @Specialization
    @TruffleBoundary
	double mul(double left, double right) {
		return left * right;
	}

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw new I4GLRuntimeException("Type error doing: " + left + " + " + right);
    }
}