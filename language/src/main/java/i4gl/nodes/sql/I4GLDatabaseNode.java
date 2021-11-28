package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.statement.I4GLStatementNode;
import i4gl.runtime.values.I4GLDatabase;

public class I4GLDatabaseNode extends I4GLStatementNode {
    private final FrameSlot slot;
    private final I4GLDatabase database;

    public I4GLDatabaseNode(final FrameSlot slot, final I4GLDatabase database) {
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
