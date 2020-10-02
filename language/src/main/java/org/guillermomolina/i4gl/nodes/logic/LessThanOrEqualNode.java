package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.BinaryExpressionNode;
import org.guillermomolina.i4gl.nodes.utils.BinaryArgumentPrimitiveTypes;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.FloatDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;

/**
 * Node representing less than or equal operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link LessThanOrEqualNodeGen}
 */
@NodeInfo(shortName = "<=")
public abstract class LessThanOrEqualNode extends BinaryExpressionNode {

    LessThanOrEqualNode() {
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, IntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, IntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(FloatDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, FloatDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(FloatDescriptor.SINGLETON, FloatDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, IntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
    }

    @Specialization
    int lessThanOrEqual(int left, int right) {
        return left <= right ? 1 : 0;
    }

	@Specialization
	int lessThanOrEqual(long left, long right) {
		return left <= right ? 1 : 0;
	}

	@Specialization
	int lessThanOrEqual(float left, float right) {
		return left <= right ? 1 : 0;
	}

	@Specialization
	int lessThanOrEqual(double left, double right) {
		return left <= right ? 1 : 0;
	}

    @Override
    public TypeDescriptor getType() {
        return IntDescriptor.SINGLETON;
    }

}
