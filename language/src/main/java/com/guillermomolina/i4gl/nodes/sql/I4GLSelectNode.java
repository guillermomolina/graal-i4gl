package com.guillermomolina.i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import com.guillermomolina.i4gl.nodes.variables.write.I4GLAssignResultsNode;
import com.guillermomolina.i4gl.runtime.exceptions.DatabaseException;
import com.guillermomolina.i4gl.runtime.values.I4GLCursor;
import com.guillermomolina.i4gl.runtime.values.I4GLDatabase;

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
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        final I4GLCursor cursor = new I4GLCursor(database, sql);
        cursor.start();

        if(cursor.next() && assignResultsNode != null) {
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