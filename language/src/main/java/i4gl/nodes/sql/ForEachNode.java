package i4gl.nodes.sql;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.write.WriteResultsNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.values.Cursor;
import i4gl.runtime.values.Sqlca;

/**
 * Node representing I4GL's foreach loop.
 */
@NodeInfo(shortName = "FOREACH", description = "The node implementing a foreach loop on a cursor")
public class ForEachNode extends StatementNode {
    private final WriteResultsNode writeResultsNode;
    @Child
    private ExpressionNode cursorVariableNode;
    @Child
    private StatementNode body;

    public ForEachNode(ExpressionNode cursorVariableNode, final WriteResultsNode writeResultsNode,
            final StatementNode body) {
        this.cursorVariableNode = cursorVariableNode;
        this.writeResultsNode = writeResultsNode;
        this.body = body;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        CompilerDirectives.transferToInterpreterAndInvalidate();
        Sqlca sqlca = Context.get(this).getSqlcaGlobalVariable();

        final Cursor cursor = (Cursor) cursorVariableNode.executeGeneric(frame);
        cursor.start(sqlca);
        while (cursor.next(sqlca)) {
            if (writeResultsNode != null) {
                writeResultsNode.setResults(cursor.getRow());
                writeResultsNode.executeVoid(frame);
            }
            body.executeVoid(frame);
        }        
        cursor.end();
    }
}
