package i4gl.nodes.sql;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.nodes.variables.write.I4GLAssignResultsNode;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.values.I4GLCursor;
import i4gl.runtime.values.I4GLRecord;

/**
 * Node representing I4GL's foreach loop.
 */
@NodeInfo(shortName = "FOREACH", description = "The node implementing a foreach loop on a cursor")
public class I4GLForEachNode extends I4GLStatementNode {
    private final I4GLAssignResultsNode assignResultsNode;
    @Child
    private I4GLExpressionNode cursorVariableNode;
    @Child
    private I4GLStatementNode body;

    public I4GLForEachNode(I4GLExpressionNode cursorVariableNode, final I4GLAssignResultsNode assignResultsNode,
            final I4GLStatementNode body) {
        this.cursorVariableNode = cursorVariableNode;
        this.assignResultsNode = assignResultsNode;
        this.body = body;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        I4GLRecord sqlca = I4GLContext.get(this).getSqlcaGlobalVariable();

        final I4GLCursor cursor = (I4GLCursor) cursorVariableNode.executeGeneric(frame);
        cursor.start(sqlca);
        while (cursor.next(sqlca)) {
            if (assignResultsNode != null) {
                assignResultsNode.setResults(cursor.getRow());
                assignResultsNode.executeVoid(frame);
            }
            body.executeVoid(frame);
        }        
        cursor.end();
    }
}
