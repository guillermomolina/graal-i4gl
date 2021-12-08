package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.nodes.variables.write.I4GLAssignResultsNode;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.exceptions.DatabaseException;
import i4gl.runtime.values.I4GLCursor;
import i4gl.runtime.values.I4GLDatabase;
import i4gl.runtime.values.I4GLRecord;

public class I4GLSelectNode extends I4GLStatementNode {
    private final String sql;
    private final I4GLAssignResultsNode assignResultsNode;
    @Child
    private I4GLExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public I4GLSelectNode(final I4GLExpressionNode databaseVariableNode, final String sql,
            final I4GLAssignResultsNode assignResultsNode) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
        this.assignResultsNode = assignResultsNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        I4GLRecord sqlca = I4GLContext.get(this).getSqlcaGlobalVariable();
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        final I4GLCursor cursor = new I4GLCursor(database, sql);
        cursor.start(sqlca);

        if (cursor.next(sqlca) && assignResultsNode != null) {
            assignResultsNode.setResults(cursor.getRow());
            assignResultsNode.executeVoid(frame);
        }

        if (cursor.next(sqlca)) {
            final String query = sql.replace("\n", "").replace("\r", "").replace("\t", "");
            throw new DatabaseException("The query \"" + query + "\" has not returned exactly one row.");
        }
        cursor.end();
    }
}