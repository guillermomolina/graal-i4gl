package i4gl.nodes.call;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.write.AssignResultsNode;
import i4gl.runtime.context.I4GLFunction;

@NodeInfo(shortName = "CALL")
public final class CallNode extends StatementNode {

    private final AssignResultsNode assignResultsNode;
    @Child
    private InvokeNode invokeNode;
    @CompilationFinal
    private I4GLFunction cachedFunction;
    @Child
    private InteropLibrary library;

    public CallNode(final InvokeNode invokeNode, final AssignResultsNode assignResultsNode) {
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
