package org.guillermomolina.i4gl.nodes.arithmetic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.BinaryExpressionNode;
import org.guillermomolina.i4gl.nodes.utils.BinaryArgumentPrimitiveTypes;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;

/**
 * Node representing I4GL's multiplication operation. If the arguments are sets, then it is understood as intersection.
 *
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link MultiplyNodeGen}
 */
@NodeInfo(shortName = "*")
public abstract class MultiplyNode extends BinaryExpressionNode {

    MultiplyNode() {
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, IntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, IntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), RealDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, RealDescriptor.SINGLETON), RealDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), SmallFloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, SmallFloatDescriptor.SINGLETON), SmallFloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, SmallFloatDescriptor.SINGLETON), SmallFloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.SINGLETON, RealDescriptor.SINGLETON), RealDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.SINGLETON, SmallFloatDescriptor.SINGLETON), RealDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, RealDescriptor.SINGLETON), RealDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, RealDescriptor.SINGLETON), RealDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.SINGLETON, RealDescriptor.SINGLETON), RealDescriptor.SINGLETON);
    }

    @Specialization
    int mul(int left, int right) {
        return Math.multiplyExact(left, right);
    }

    @Specialization
    long mul(long left, long right) {
        return Math.multiplyExact(left, right);
    }

	@Specialization
	float mul(float left, float right) {
		return left * right;
	}

	@Specialization
	double mul(double left, double right) {
		return left * right;
	}
}
