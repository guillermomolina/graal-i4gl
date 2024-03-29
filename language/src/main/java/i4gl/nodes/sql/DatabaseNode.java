package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.values.Database;

public class DatabaseNode extends StatementNode {
    private final FrameSlot slot;
    private final Database database;

    public DatabaseNode(final FrameSlot slot, final Database database) {
        this.slot = slot;
        this.database = database;
    }

    public FrameSlot getSlot() {
        return slot;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setObject(slot, database);
        database.connect();
    }
}
