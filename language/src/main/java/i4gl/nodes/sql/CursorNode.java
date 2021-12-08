package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.values.Cursor;
import i4gl.runtime.values.Database;

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
        final Database database = (Database) databaseVariableNode.executeGeneric(frame);
        frame.setObject(slot, new Cursor(database, sql));
    }
}
