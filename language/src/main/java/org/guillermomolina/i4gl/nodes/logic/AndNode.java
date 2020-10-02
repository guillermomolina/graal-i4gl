package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.BinaryExpressionNode;
import org.guillermomolina.i4gl.nodes.utils.BinaryArgumentPrimitiveTypes;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.FloatDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;

/**
 * Node representing logical and operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AndNodeGen}
 */
@NodeInfo(shortName = "and")
public abstract class AndNode extends BinaryExpressionNode {

    AndNode() {
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, IntDescriptor.SINGLETON), IntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, IntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), BigIntDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(FloatDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, FloatDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, BigIntDescriptor.SINGLETON), SmallFloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(BigIntDescriptor.SINGLETON, SmallFloatDescriptor.SINGLETON), SmallFloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, SmallFloatDescriptor.SINGLETON), SmallFloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(FloatDescriptor.SINGLETON, FloatDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(FloatDescriptor.SINGLETON, SmallFloatDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, FloatDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(SmallFloatDescriptor.SINGLETON, FloatDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(FloatDescriptor.SINGLETON, FloatDescriptor.SINGLETON), FloatDescriptor.SINGLETON);
    }

    @Specialization
    int and(int left, int right) {
        return (left != 0 && right != 0) ? 1 : 0;
    }

	@Specialization
	int and(long left, long right) {
		return (left != 0 && right != 0) ? 1 : 0;
	}

	@Specialization
	int and(float left, float right) {
		return (left != '0' && right != '0') ? 1 : 0;
	}

	@Specialization
	int and(double left, double right) {
		return (left != 0 && right != 0) ? 1 : 0;
	}
}
