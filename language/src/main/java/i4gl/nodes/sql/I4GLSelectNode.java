package i4gl.nodes.sql;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.nodes.variables.write.I4GLAssignResultsNode;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.exceptions.DatabaseException;
import i4gl.runtime.exceptions.I4GLRuntimeException;
import i4gl.runtime.values.I4GLCursor;
import i4gl.runtime.values.I4GLDatabase;
import i4gl.runtime.values.I4GLRecord;

public class I4GLSelectNode extends I4GLStatementNode {
    private final String sql;
    final FrameSlot sqlcaFrameSlot;
    private final I4GLAssignResultsNode assignResultsNode;
    @Child
    private I4GLExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public I4GLSelectNode(final I4GLExpressionNode databaseVariableNode, final String sql,
            final I4GLAssignResultsNode assignResultsNode, final FrameSlot sqlcaFrameSlot) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
        this.assignResultsNode = assignResultsNode;
        this.sqlcaFrameSlot = sqlcaFrameSlot;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        getSqlca(frame);
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        final I4GLCursor cursor = new I4GLCursor(database, sql);
        cursor.start();

        /*getSqlca(frame).put("sqlcode", 0);
        I4GLIntArray sqlerrd = (I4GLIntArray) getSqlca(frame).get("sqlerrd");
        sqlerrd.setValueAt(2, 10);*/

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

    private I4GLRecord getSqlca(VirtualFrame frame) {
        try {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            var globalFrame = I4GLContext.get(this).getModuleFrame("GLOBAL");
            Object sqlca = globalFrame.getObject(sqlcaFrameSlot);
            return (I4GLRecord) sqlca;
        } catch (FrameSlotTypeException e) {
            throw new I4GLRuntimeException("sqlca variable is not of the correct type");
        }
    }
}