package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;

public class I4GLConnectToDatabaseNode extends I4GLStatementNode {
    @SuppressWarnings("unused")
    private final FrameSlot slot;

    public I4GLConnectToDatabaseNode(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw new NotImplementedException();
    }
}
