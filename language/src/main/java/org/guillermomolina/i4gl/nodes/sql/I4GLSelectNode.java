package org.guillermomolina.i4gl.nodes.sql;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadFromResultNode;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseException;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;
import org.guillermomolina.i4gl.runtime.values.I4GLCursor;
import org.guillermomolina.i4gl.runtime.values.I4GLDatabase;

public class I4GLSelectNode extends I4GLStatementNode {
    private  final String sql;
    private final I4GLReadFromResultNode[] readResultNodes;
    private final I4GLStatementNode[] assignResultNodes;
    @Child
    private I4GLExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;

    public I4GLSelectNode(final I4GLExpressionNode databaseVariableNode, final String sql, final I4GLReadFromResultNode[] readResultNodes,
    final I4GLStatementNode[] assignResultNodes) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
        this.readResultNodes = readResultNodes;
        this.assignResultNodes = assignResultNodes;
    }

    public void evaluateResult(VirtualFrame frame, Object[] results) {
        if (results.length != readResultNodes.length) {
            throw new IncorrectNumberOfReturnValuesException(assignResultNodes.length, results.length);
        }
        for (int i = 0; i < readResultNodes.length; i++) {
            readResultNodes[i].setResult(results[i]);
        }
        if (assignResultNodes.length != 0) {
            evaluateResults(frame, results);
        }    
    }

    @ExplodeLoop
    private void evaluateResults(VirtualFrame frame, Object[] results) {
        CompilerAsserts.compilationConstant(assignResultNodes.length);

        for (int i = 0; i < assignResultNodes.length; i++) {
            assignResultNodes[i].executeVoid(frame);
        }
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        final I4GLCursor cursor = new I4GLCursor(database, sql);
        cursor.start();

        Object[] result = null;
        if(cursor.next()) {
            result = cursor.getRow();
        }
        if (cursor.next()) {
            final String query = sql.replace("\n", "").replace("\r", "").replace("\t", "");
            throw new DatabaseException("The query \"" + query + "\" has not returned exactly one row.");
        }
        cursor.end();
        
        if(result != null) {
            evaluateResult(frame, result);
        }
    }
}