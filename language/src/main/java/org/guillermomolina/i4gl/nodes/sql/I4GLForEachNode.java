package org.guillermomolina.i4gl.nodes.sql;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.nodes.variables.read.I4GLReadFromResultNode;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;
import org.guillermomolina.i4gl.runtime.values.I4GLCursor;

/**
 * Node representing I4GL's foreach loop.
 */
@NodeInfo(shortName = "FOREACH", description = "The node implementing a foreach loop on a cursor")
public class I4GLForEachNode extends I4GLStatementNode {
    private final I4GLReadFromResultNode[] readResultNodes;
    private final I4GLStatementNode[] assignResultNodes;
    @Child
    private I4GLExpressionNode cursorVariableNode;
    @Child
    private I4GLStatementNode body;

    public I4GLForEachNode(I4GLExpressionNode cursorVariableNode, final I4GLReadFromResultNode[] readResultNodes,
            final I4GLStatementNode[] assignResultNodes, final I4GLStatementNode body) {
        this.cursorVariableNode = cursorVariableNode;
        this.readResultNodes = readResultNodes;
        this.assignResultNodes = assignResultNodes;
        this.body = body;
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
        final I4GLCursor cursor = (I4GLCursor) cursorVariableNode.executeGeneric(frame);
        cursor.start();
        while (cursor.next()) {
            evaluateResult(frame, cursor.getRow());
            body.executeVoid(frame);
        }
        cursor.end();
    }
}
