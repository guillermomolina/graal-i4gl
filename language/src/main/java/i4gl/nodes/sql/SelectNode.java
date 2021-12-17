package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.DatabaseException;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.nodes.variables.write.WriteResultsNode;
import i4gl.runtime.values.Cursor;
import i4gl.runtime.values.Database;

public class SelectNode extends StatementNode {
    private final String sql;
    private final WriteResultsNode assignResultsNode;
    @Child
    private ExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public SelectNode(final ExpressionNode databaseVariableNode, final String sql,
            final WriteResultsNode assignResultsNode) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
        this.assignResultsNode = assignResultsNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final Database database = (Database) databaseVariableNode.executeGeneric(frame);
        final Cursor cursor = new Cursor(database, sql);
        cursor.start();

        if (cursor.next() && assignResultsNode != null) {
            assignResultsNode.setResults(cursor.getRow());
            assignResultsNode.executeVoid(frame);
        }

        if (cursor.next()) {
            final String query = sql.replace("\n", "").replace("\r", "").replace("\t", "");
            throw new DatabaseException("The query \"" + query + "\" has not returned exactly one row.");
        }
        cursor.end();
    }
}