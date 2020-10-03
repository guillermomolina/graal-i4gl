package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
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
import org.guillermomolina.i4gl.runtime.customvalues.ReturnValue;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;

@NodeInfo(shortName = "call")
public final class I4GLCallNode extends I4GLStatementNode {

    private final I4GLLanguage language;
    private final String functionIdentifier;
    private final FrameSlot[] resultSlots;
    @Children
    private final I4GLExpressionNode[] argumentNodes;
    @CompilerDirectives.CompilationFinal
    private CallTarget function;
    @Child
    private InteropLibrary library;

    public I4GLCallNode(I4GLLanguage language, String identifier, I4GLExpressionNode[] argumentNodes, FrameSlot[] resultSlots) {
        this.language = language;
        this.functionIdentifier = identifier;
        this.argumentNodes = argumentNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.resultSlots = resultSlots;
    }

    private CallTarget getFunction() {
        return language.getFunction(this.functionIdentifier);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        if (function == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            function = getFunction();
        }
        Object[] argumentValues = this.evaluateArguments(frame);
        ReturnValue returnValue = (ReturnValue) function.call(argumentValues);
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
    public void evaluateResult(VirtualFrame frame, ReturnValue returnValue) {
        if (returnValue.getSize() != resultSlots.length) {
            throw new IncorrectNumberOfReturnValuesException(resultSlots.length, returnValue.getSize());
        }
        for (int index = 0; index < resultSlots.length; index++) {
            final Object result = returnValue.getValueAt(index);
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
