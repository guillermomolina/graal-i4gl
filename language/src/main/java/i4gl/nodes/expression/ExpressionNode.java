package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import i4gl.I4GLTypeSystem;
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
@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(description = "Abstract class for all nodes that return value")
@GenerateWrapper
public abstract class ExpressionNode extends StatementNode {

    private boolean hasExpressionTag;

    /**
     * Returns type of the expression. This method is mainly used for compile time type checking.
     */
    public abstract BaseType getType();

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

    protected boolean isChar() {
        return getType() == Char1Type.SINGLETON;
    }

    protected boolean isSmallInt() {
        return getType() == SmallIntType.SINGLETON;
    }

    protected boolean isInt() {
        return getType() == IntType.SINGLETON;
    }

    protected boolean isBigInt() {
        return getType() == BigIntType.SINGLETON;
    }

    protected boolean isSmallFloat() {
        return getType() == SmallFloatType.SINGLETON;
    }

    protected boolean isFloat() {
        return getType() == FloatType.SINGLETON;
    }

    protected boolean isDate() {
        return getType() == DateType.SINGLETON;
    }

    protected boolean isDecimal() {
        return getType() instanceof DecimalType;
    }

    protected boolean isRecord() {
        return getType() instanceof RecordType;
    }
}
