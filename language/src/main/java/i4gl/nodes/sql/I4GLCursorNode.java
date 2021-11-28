package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.runtime.values.I4GLCursor;
import i4gl.runtime.values.I4GLDatabase;

public class I4GLCursorNode extends I4GLStatementNode {
    @Child
    private I4GLExpressionNode databaseVariableNode;
    private final FrameSlot slot;
    private final String sql;

    public I4GLCursorNode(final I4GLExpressionNode databaseVariableNode, final String sql, final FrameSlot slot) {
        this.slot = slot;
        this.databaseVariableNode = databaseVariableNode;
        this.sql = sql;
    }
        
    public FrameSlot getSlot() {
        return slot;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        frame.setObject(slot, new I4GLCursor(database, sql));
    }
}
