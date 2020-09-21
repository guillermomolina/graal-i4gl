package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.runtime.customvalues.PointerValue;
import org.guillermomolina.i4gl.runtime.customvalues.SetTypeValue;
import org.guillermomolina.i4gl.nodes.utils.BinaryArgumentPrimitiveTypes;
import org.guillermomolina.i4gl.nodes.BinaryExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.*;

/**
 * Node representing equals operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link EqualsNodeGen}
 */
@NodeInfo(shortName = "=")
public abstract class EqualsNode extends BinaryExpressionNode {

    EqualsNode() {
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.getInstance(), IntDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), LongDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.getInstance(), RealDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(CharDescriptor.getInstance(), CharDescriptor.getInstance()), IntDescriptor.getInstance());
    }

    @Specialization
    protected int equals(int left, int right) {
        return left == right ? 1 : 0;
    }

    @Specialization
    protected int equals(long left, long right) {
        return left == right ? 1 : 0;
    }

	@Specialization
	protected int equals(double left, double right) {
		return left == right ? 1 : 0;
	}

	@Specialization
    protected int equals(char left, char right) {
	    return left == right ? 1 : 0;
    }

	@Specialization
	protected int equals(SetTypeValue left, SetTypeValue right) {
	    return left.equals(right) ? 1 : 0;
    }

    @Specialization
    protected int equals(PointerValue left, PointerValue right) {
	    return left.equals(right) ? 1 : 0;
    }

    // TODO: what about record type? file type?

    @Override
    public boolean verifyNonPrimitiveArgumentTypes(TypeDescriptor leftType, TypeDescriptor rightType) {
        return this.verifyBothCompatibleSetTypes(leftType, rightType) ||
                this.verifyBothCompatiblePointerTypes(leftType, rightType);
    }

    @Override
    public TypeDescriptor getType() {
        return IntDescriptor.getInstance();
    }

}
