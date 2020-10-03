package org.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.guillermomolina.i4gl.I4GLTypes;
import org.guillermomolina.i4gl.I4GLTypesGen;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.DoubleDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;

/**
 * This is a base node class for each node that represents an expression (returns a value after its execution). Not all
 * the specialized execute{Type} methods are implemented because we do not really need them since we are using Truffle's
 * specializations.
 */
@TypeSystemReference(I4GLTypes.class)
@NodeInfo(description = "Abstract class for all nodes that return value")
@GenerateWrapper
public abstract class I4GLExpressionNode extends I4GLStatementNode {

    private boolean hasExpressionTag;

    /**
     * Returns type of the expression. This method is mainly used for compile time type checking.
     */
    public abstract I4GLTypeDescriptor getType();

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
	    return I4GLTypesGen.expectInteger(executeGeneric(frame));
    }

	public long executeBigInt(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypesGen.expectLong(executeGeneric(frame));
	}

	public double executeSmallFloat(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypesGen.expectFloat(executeGeneric(frame));
	}

	public double executeFloat(VirtualFrame frame) throws UnexpectedResultException {
		return I4GLTypesGen.expectDouble(executeGeneric(frame));
	}

    protected boolean isInt() {
        return getType() == IntDescriptor.SINGLETON;
    }

    protected boolean isBigInt() {
        return getType() == BigIntDescriptor.SINGLETON;
    }

    protected boolean isSmallFloat() {
        return getType() == SmallFloatDescriptor.SINGLETON;
    }

    protected boolean isDouble() {
        return getType() == DoubleDescriptor.SINGLETON;
    }
}
