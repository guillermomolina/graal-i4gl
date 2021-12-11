package i4gl.nodes.builtin;

import java.util.Arrays;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import i4gl.exceptions.I4GLRuntimeException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.context.FunctionRegistry;

/**
 * Base class for all builtin functions. It contains the Truffle DSL annotation
 * {@link NodeChild} that defines the function arguments.<br>
 * The builtin functions are registered in {@link Context#installBuiltins}.
 * Every builtin node subclass is instantiated there, wrapped into a function,
 * and added to the {@link FunctionRegistry}. This ensures that builtin
 * functions can be called like user-defined functions; there is no special
 * function lookup or call node for builtin functions.
 */
@NodeChild(value = "arguments", type = ExpressionNode[].class)
@GenerateNodeFactory
public abstract class BuiltinNode extends ExpressionNode {

    @Override
    public final Object executeGeneric(VirtualFrame frame) {
        try {
            return execute(frame);
        } catch (UnsupportedSpecializationException e) {
            throw new I4GLRuntimeException(
                    "Type error " + e.getNode().toString() + Arrays.toString(e.getSuppliedValues()));
        }
    }

    @Override
    public final int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeInt(frame);
    }

    @Override
    public final long executeBigInt(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeBigInt(frame);
    }

    @Override
    public final float executeSmallFloat(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeSmallFloat(frame);
    }

    @Override
    public final double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return super.executeDouble(frame);
    }

    @Override
    public final void executeVoid(VirtualFrame frame) {
        super.executeVoid(frame);
    }

    protected abstract Object execute(VirtualFrame frame);

}
