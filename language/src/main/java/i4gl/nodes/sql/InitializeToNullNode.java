package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.write.WriteResultsNode;

public class InitializeToNullNode extends StatementNode {
    private final WriteResultsNode assignResultsNode;
    @Child
    private InteropLibrary interop;

    public InitializeToNullNode(final WriteResultsNode assignResultsNode) {
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.assignResultsNode = assignResultsNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        assignResultsNode.setResultsToNull();
        assignResultsNode.executeVoid(frame);
    }
}