package org.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.guillermomolina.i4gl.I4GLTypesGen;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.*;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.*;

/**
 * This is a base node class for each node that represents an expression (returns a value after its execution). Not all
 * the specialized execute{Type} methods are implemented because we do not really need them since we are using Truffle's
 * specializations.
 */
@NodeInfo(description = "Abstract class for all nodes that return value")
public abstract class ExpressionNode extends StatementNode {

    /**
     * Returns type of the expression. This method is mainly used for compile time type checking.
     */
    public abstract TypeDescriptor getType();

    @Override
    public void executeVoid(VirtualFrame virtualFrame) {
        executeGeneric(virtualFrame);
    }

	public abstract Object executeGeneric(VirtualFrame frame);

	public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
	    return I4GLTypesGen.expectInteger(executeGeneric(frame));
    }

	public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypesGen.expectLong(executeGeneric(frame));
	}

	public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypesGen.expectDouble(executeGeneric(frame));
	}

	public char executeChar(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypesGen.expectCharacter(executeGeneric(frame));
	}

    protected boolean isInt() {
        return getType() == IntDescriptor.getInstance() || getType() instanceof IntConstantDescriptor;
    }

    protected boolean isLong() {
        return getType() == LongDescriptor.getInstance() || getType() instanceof LongConstantDescriptor;
    }

    protected boolean isDouble() {
        return getType() == RealDescriptor.getInstance() || getType() instanceof RealConstantDescriptor;
    }

    protected boolean isChar() {
        return getType() == CharDescriptor.getInstance() || getType() instanceof CharConstantDescriptor;
    }
}
