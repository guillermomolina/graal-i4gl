package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.write.AssignResultsNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.exceptions.DatabaseException;
import i4gl.runtime.values.Cursor;
import i4gl.runtime.values.Database;
import i4gl.runtime.values.Record;

public class SelectNode extends StatementNode {
    private final String sql;
    private final AssignResultsNode assignResultsNode;
    @Child
    private ExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public SelectNode(final ExpressionNode databaseVariableNode, final String sql,
            final AssignResultsNode assignResultsNode) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
        this.assignResultsNode = assignResultsNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Record sqlca = Context.get(this).getSqlcaGlobalVariable();
        final Database database = (Database) databaseVariableNode.executeGeneric(frame);
        final Cursor cursor = new Cursor(database, sql);
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