package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.values.I4GLDatabase;

public class I4GLSelectNode extends I4GLStatementNode {
    @Child
    private I4GLExpressionNode getDatabaseVariableNode;
    @Child
    private InteropLibrary interop;
    private  final String sql;

    public I4GLSelectNode(final I4GLExpressionNode getDatabaseVariableNode, final String sql) {
        this.getDatabaseVariableNode = getDatabaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final I4GLDatabase database = (I4GLDatabase) getDatabaseVariableNode.executeGeneric(frame);
        database.execute(sql);
    }
}
