package i4gl.nodes.sql;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.write.AssignResultsNode;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.values.I4GLCursor;
import i4gl.runtime.values.I4GLRecord;

/**
 * Node representing I4GL's foreach loop.
 */
@NodeInfo(shortName = "FOREACH", description = "The node implementing a foreach loop on a cursor")
public class ForEachNode extends StatementNode {
    private final AssignResultsNode assignResultsNode;
    @Child
    private ExpressionNode cursorVariableNode;
    @Child
    private StatementNode body;

    public ForEachNode(ExpressionNode cursorVariableNode, final AssignResultsNode assignResultsNode,
            final StatementNode body) {
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
