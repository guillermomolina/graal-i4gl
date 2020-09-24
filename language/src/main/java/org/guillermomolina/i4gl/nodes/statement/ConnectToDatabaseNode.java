package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;

public class ConnectToDatabaseNode extends StatementNode {
    private final FrameSlot slot;

    public ConnectToDatabaseNode(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw new NotImplementedException();
    }
}
