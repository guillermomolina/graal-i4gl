package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.write.I4GLAssignResultsNode;
import org.guillermomolina.i4gl.runtime.context.I4GLFunction;

@NodeInfo(shortName = "CALL")
public final class I4GLCallNode extends I4GLStatementNode {

    private final I4GLAssignResultsNode assignResultsNode;
    @Child
    private I4GLInvokeNode invokeNode;
    @CompilationFinal
    private I4GLFunction cachedFunction;
    @Child
    private InteropLibrary library;

    public I4GLCallNode(final I4GLInvokeNode invokeNode, final I4GLAssignResultsNode assignResultsNode) {
        this.invokeNode = invokeNode;
        this.assignResultsNode = assignResultsNode;
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final Object callOutput = invokeNode.invoke(frame);
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

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.CallTag.class) {
            return true;
        }
        return super.hasTag(tag);
    }
}
