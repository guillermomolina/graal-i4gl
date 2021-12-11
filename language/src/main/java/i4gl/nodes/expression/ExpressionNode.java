package i4gl.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import i4gl.I4GLTypeSystemGen;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.Char1Type;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.RecordType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.DecimalType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;

/**
 * This is a base node class for each node that represents an expression (returns a value after its execution). Not all
 * the specialized execute{Type} methods are implemented because we do not really need them since we are using Truffle's
 * specializations.
 */
@NodeInfo(description = "Abstract class for all nodes that return value")
@GenerateWrapper
public abstract class ExpressionNode extends StatementNode {

    private boolean hasExpressionTag;

    /**
     * Returns type of the expression. This method is mainly used for compile time type checking.
     */
    public abstract BaseType getReturnType();

	public abstract Object executeGeneric(VirtualFrame frame);

    @Override
    public void executeVoid(VirtualFrame virtualFrame) {
        executeGeneric(virtualFrame);
    }

    @Override
    public WrapperNode createWrapper(ProbeNode probe) {
        return new ExpressionNodeWrapper(this, probe);
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.ExpressionTag.class) {
            return hasExpressionTag;
        }
        return super.hasTag(tag);
    }

    /**
     * Marks this node as being a {@link StandardTags.ExpressionTag} for instrumentation purposes.
     */
    public final void addExpressionTag() {
        hasExpressionTag = true;
    }

	public short executeSmallInt(VirtualFrame frame) throws UnexpectedResultException {
	    return I4GLTypeSystemGen.expectShort(executeGeneric(frame));
    }

	public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
	    return I4GLTypeSystemGen.expectInteger(executeGeneric(frame));
    }

	public long executeBigInt(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypeSystemGen.expectLong(executeGeneric(frame));
	}

	public float executeSmallFloat(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypeSystemGen.expectFloat(executeGeneric(frame));
	}

	public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypeSystemGen.expectDouble(executeGeneric(frame));
	}

    protected boolean returnsChar() {
        return getReturnType() == Char1Type.SINGLETON;
    }

    protected boolean returnsSmallInt() {
        return getReturnType() == SmallIntType.SINGLETON;
    }

    protected boolean returnsInt() {
        return getReturnType() == IntType.SINGLETON;
    }

    protected boolean returnsBigInt() {
        return getReturnType() == BigIntType.SINGLETON;
    }

    protected boolean returnsSmallFloat() {
        return getReturnType() == SmallFloatType.SINGLETON;
    }

    protected boolean returnsFloat() {
        return getReturnType() == FloatType.SINGLETON;
    }

    protected boolean returnsDate() {
        return getReturnType() == DateType.SINGLETON;
    }

    protected boolean returnsDecimal() {
        return getReturnType() instanceof DecimalType;
    }

    protected boolean returnsRecord() {
        return getReturnType() instanceof RecordType;
    }
}
