package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignResultsNode;
import org.guillermomolina.i4gl.runtime.context.I4GLFunction;

@NodeInfo(shortName = "CALL")
public final class I4GLCallNode extends I4GLStatementNode {

    private final String functionIdentifier;
    private final I4GLAssignResultsNode assignResultsNode;
    @Children
    private final I4GLExpressionNode[] argumentNodes;
    @CompilationFinal
    private I4GLFunction cachedFunction;
    @Child
    private InteropLibrary library;

    public I4GLCallNode(final String identifier, final I4GLExpressionNode[] argumentNodes,
            final I4GLAssignResultsNode assignResultsNode) {
        this.functionIdentifier = identifier;
        this.argumentNodes = argumentNodes;
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.assignResultsNode = assignResultsNode;
    }

    private I4GLFunction getFunction() {
        return lookupContextReference(I4GLLanguage.class).get().getFunctionRegistry().lookup(functionIdentifier, true);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        if (cachedFunction == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            cachedFunction = getFunction();
        }
        Object[] argumentValues = this.evaluateArguments(frame);
        CallTarget function = cachedFunction.getCallTarget();
        final Object callOutput = function.call(argumentValues);
        if (assignResultsNode != null) {
            Object[] results;
            if (callOutput instanceof Object[]) {
                results = (Object[]) callOutput;
            } else {
                results = new Object[1];
                results[0] = callOutput;
            }
            assignResultsNode.setResults(results);
            assignResultsNode.executeVoid(frame);
        }
    }

    @ExplodeLoop
    private Object[] evaluateArguments(VirtualFrame frame) {
        CompilerAsserts.compilationConstant(argumentNodes.length);

        Object[] argumentValues = new Object[argumentNodes.length];
        for (int i = 0; i < argumentNodes.length; i++) {
            argumentValues[i] = argumentNodes[i].executeGeneric(frame);
        }

        return argumentValues;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.CallTag.class) {
            return true;
        }
        return super.hasTag(tag);
    }
}
