package com.guillermomolina.i4gl.nodes.sql;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.guillermomolina.i4gl.nodes.expression.I4GLExpressionNode;
import com.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import com.guillermomolina.i4gl.nodes.variables.write.I4GLAssignResultsNode;
import com.guillermomolina.i4gl.runtime.values.I4GLCursor;

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
        final I4GLCursor cursor = (I4GLCursor) cursorVariableNode.executeGeneric(frame);
        cursor.start();
        while (cursor.next()) {
            if (assignResultsNode != null) {
                assignResultsNode.setResults(cursor.getRow());
                assignResultsNode.executeVoid(frame);
            }
            body.executeVoid(frame);
        }
        cursor.end();
    }
}
