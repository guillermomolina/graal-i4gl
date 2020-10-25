package org.guillermomolina.i4gl.nodes.builtin;

import java.util.Arrays;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.I4GLContext;
import org.guillermomolina.i4gl.runtime.I4GLFunctionRegistry;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Base class for all builtin functions. It contains the Truffle DSL annotation
 * {@link NodeChild} that defines the function arguments.<br>
 * The builtin functions are registered in {@link I4GLContext#installBuiltins}.
 * Every builtin node subclass is instantiated there, wrapped into a function,
 * and added to the {@link I4GLFunctionRegistry}. This ensures that builtin
 * functions can be called like user-defined functions; there is no special
 * function lookup or call node for builtin functions.
 */
@NodeChild(value = "arguments", type = I4GLExpressionNode[].class)
@GenerateNodeFactory
public abstract class I4GLBuiltinNode extends I4GLExpressionNode {

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
