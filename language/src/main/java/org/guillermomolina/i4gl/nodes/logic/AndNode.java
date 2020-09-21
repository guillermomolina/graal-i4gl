package org.guillermomolina.i4gl.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.runtime.customvalues.EnumValue;
import org.guillermomolina.i4gl.runtime.customvalues.SetTypeValue;
import org.guillermomolina.i4gl.nodes.utils.BinaryArgumentPrimitiveTypes;
import org.guillermomolina.i4gl.nodes.BinaryExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.GenericEnumTypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.*;

/**
 * Node representing logical and operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AndNodeGen}
 */
@NodeInfo(shortName = "and")
public abstract class AndNode extends BinaryExpressionNode {

    AndNode() {
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.getInstance(), IntDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), LongDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), IntDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(IntDescriptor.getInstance(), LongDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), LongDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.getInstance(), LongDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(LongDescriptor.getInstance(), RealDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(RealDescriptor.getInstance(), RealDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(CharDescriptor.getInstance(), CharDescriptor.getInstance()), IntDescriptor.getInstance());
        this.typeTable.put(new BinaryArgumentPrimitiveTypes(GenericEnumTypeDescriptor.getInstance(), GenericEnumTypeDescriptor.getInstance()), IntDescriptor.getInstance());
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
	int and(double left, double right) {
		return (left != 0 && right != 0) ? 1 : 0;
	}

	@Specialization
	int and(char left, char right) {
		return (left != '0' && right != '0') ? 1 : 0;
	}

	@Specialization
	int and(SetTypeValue left, SetTypeValue right) {
		return (left.getSize() != 0 && right.getSize() != 0) ? 1 : 0;
	}

	@Specialization
	int and(EnumValue left, EnumValue right) {
		return left == right ? 1 : 0;
	}

	@Override
    public boolean verifyNonPrimitiveArgumentTypes(TypeDescriptor leftType, TypeDescriptor rightType) {
        return this.verifyBothCompatibleSetTypes(leftType, rightType);
    }

    @Override
    public TypeDescriptor getType() {
        return IntDescriptor.getInstance();
    }

}
