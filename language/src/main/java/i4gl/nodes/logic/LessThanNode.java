package i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.BinaryExpressionNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;

/**
 * Node representing less than operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link LessThanNodeGen}
 */
@NodeInfo(shortName = "<")
public abstract class LessThanNode extends BinaryExpressionNode {
    @Specialization
    int lessThan(short left, short right) {
        return left < right ? 1 : 0;
    }
    
    @Specialization
    int lessThan(int left, int right) {
        return left < right ? 1 : 0;
    }

	@Specialization
	int lessThan(long left, long right) {
		return left < right ? 1 : 0;
	}

	@Specialization
	int lessThan(float left, float right) {
		return left < right ? 1 : 0;
	}

	@Specialization
	int lessThan(double left, double right) {
		return left < right ? 1 : 0;
	}

    @Override
    public BaseType getReturnType() {
        return IntType.SINGLETON;
    }

}