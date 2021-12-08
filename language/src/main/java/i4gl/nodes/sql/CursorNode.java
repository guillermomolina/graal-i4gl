package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.values.I4GLCursor;
import i4gl.runtime.values.I4GLDatabase;

public class CursorNode extends StatementNode {
    @Child
    private ExpressionNode databaseVariableNode;
    private final FrameSlot slot;
    private final String sql;

    public CursorNode(final ExpressionNode databaseVariableNode, final String sql, final FrameSlot slot) {
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
