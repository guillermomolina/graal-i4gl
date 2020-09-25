package org.guillermomolina.i4gl.nodes.arithmetic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.BinaryExpressionNode;
import org.guillermomolina.i4gl.nodes.utils.BinaryArgumentPrimitiveTypes;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;

/**
 * Node representing modulo operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link ModuloNodeGen}
 */
@NodeInfo(shortName = "mod")
public abstract class ModuloNode extends BinaryExpressionNode {

    ModuloNode() {
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.getInstance(), IntDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), IntDescriptor.getInstance()), LongDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.getInstance(), LongDescriptor.getInstance()), LongDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), LongDescriptor.getInstance()), LongDescriptor.getInstance());
    }

    @Specialization
    protected int mod(int left, int right) {
        return left % right;
    }

	@Specialization
	protected long mod(long left, long right) {
		return left % right;
	}
}
