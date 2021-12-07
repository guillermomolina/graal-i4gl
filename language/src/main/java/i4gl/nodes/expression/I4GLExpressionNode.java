package i4gl.nodes.expression;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import i4gl.nodes.I4GLTypeSystem;
import i4gl.nodes.I4GLTypeSystemGen;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLChar1Type;
import i4gl.runtime.types.primitive.I4GLBigIntType;
import i4gl.runtime.types.primitive.I4GLFloatType;
import i4gl.runtime.types.primitive.I4GLIntType;
import i4gl.runtime.types.primitive.I4GLSmallFloatType;
import i4gl.runtime.types.primitive.I4GLSmallIntType;

/**
 * This is a base node class for each node that represents an expression (returns a value after its execution). Not all
 * the specialized execute{Type} methods are implemented because we do not really need them since we are using Truffle's
 * specializations.
 */
@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(description = "Abstract class for all nodes that return value")
@GenerateWrapper
public abstract class I4GLExpressionNode extends I4GLStatementNode {

    private boolean hasExpressionTag;

    /**
     * Returns type of the expression. This method is mainly used for compile time type checking.
     */
    public abstract I4GLType getType();

	public abstract Object executeGeneric(VirtualFrame frame);

    @Override
    public void executeVoid(VirtualFrame virtualFrame) {
        executeGeneric(virtualFrame);
    }

    @Override
    public WrapperNode createWrapper(ProbeNode probe) {
        return new I4GLExpressionNodeWrapper(this, probe);
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
        return getType() == I4GLChar1Type.SINGLETON;
    }

    protected boolean isSmallInt() {
        return getType() == I4GLSmallIntType.SINGLETON;
    }

    protected boolean isInt() {
        return getType() == I4GLIntType.SINGLETON;
    }

    protected boolean isBigInt() {
        return getType() == I4GLBigIntType.SINGLETON;
    }

    protected boolean isSmallFloat() {
        return getType() == I4GLSmallFloatType.SINGLETON;
    }

    protected boolean isFloat() {
        return getType() == I4GLFloatType.SINGLETON;
    }
}
