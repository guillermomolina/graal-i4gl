package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.I4GLFunction;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;

@NodeInfo(shortName = "call")
public final class I4GLCallNode extends I4GLStatementNode {

    private final String functionIdentifier;
    private final FrameSlot[] resultSlots;
    @Children
    private final I4GLExpressionNode[] argumentNodes;
    @CompilationFinal private I4GLFunction cachedFunction;
    @Child
    private InteropLibrary library;

    public I4GLCallNode(String identifier, I4GLExpressionNode[] argumentNodes, FrameSlot[] resultSlots) {
        this.functionIdentifier = identifier;
        this.argumentNodes = argumentNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.resultSlots = resultSlots;
    }

    private I4GLFunction getFunction() {
        return lookupContextReference(I4GLLanguage.class).get().getFunctionRegistry().lookup(functionIdentifier, true);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        if (cachedFunction== null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedFunction = getFunction();
        }
        Object[] argumentValues = this.evaluateArguments(frame);
        CallTarget function = cachedFunction.getCallTarget();
        Object[] returnValue = (Object[]) function.call(argumentValues);
        evaluateResult(frame, returnValue);
    }

    @ExplodeLoop
    private Object[] evaluateArguments(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(argumentNodes.length);

        Object[] argumentValues = new Object[argumentNodes.length + 1];
        argumentValues[0] = frame;
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i + 1] = argumentNodes[i].executeGeneric(frame);
        }

        return argumentValues;
    }

    @SuppressWarnings("deprecation")
    public void evaluateResult(VirtualFrame frame, Object[] returnValue) {
        if (returnValue.length != resultSlots.length) {
            throw new IncorrectNumberOfReturnValuesException(resultSlots.length, returnValue.length);
        }
        for (int index = 0; index < resultSlots.length; index++) {
            final Object result = returnValue[index];
            final FrameSlot slot = resultSlots[index];
            switch (slot.getKind()) {
                case Int:
                    frame.setInt(slot, (int) result);
                    break;
                case Long:
                    frame.setLong(slot, (long) result);
                    break;
                case Float:
                    frame.setFloat(slot, (float) result);
                    break;
                case Double:
                    frame.setDouble(slot, (double) result);
                    break;
                case Object:
                    frame.setObject(slot, result);
                    break;
                default:
            }
        }
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.CallTag.class) {
            return true;
        }
        return super.hasTag(tag);
    }
}
