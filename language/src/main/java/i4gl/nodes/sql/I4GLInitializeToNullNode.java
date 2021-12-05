package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.nodes.variables.write.I4GLAssignResultsNode;

public class I4GLInitializeToNullNode extends I4GLStatementNode {
    private final I4GLAssignResultsNode assignResultsNode;
    @Child
    private InteropLibrary interop;

    public I4GLInitializeToNullNode(final I4GLAssignResultsNode assignResultsNode) {
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.assignResultsNode = assignResultsNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        assignResultsNode.setResultsToNull();
        assignResultsNode.executeVoid(frame);
    }
}