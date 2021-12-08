package i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.values.I4GLDatabase;
import i4gl.runtime.values.I4GLRecord;

public class DatabaseNode extends StatementNode {
    private final FrameSlot slot;
    private final I4GLDatabase database;

    public DatabaseNode(final FrameSlot slot, final I4GLDatabase database) {
        this.slot = slot;
        this.database = database;
    }

    public FrameSlot getSlot() {
        return slot;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        I4GLRecord sqlca = I4GLContext.get(this).getSqlcaGlobalVariable();
        frame.setObject(slot, database);
        database.connect(sqlca);
    }
}
