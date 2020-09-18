package org.guillermomolina.i4gl.nodes.call;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.runtime.customvalues.Reference;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;

/**
 * This node is used to initialize reference passed variable in called function.
 */
public class ReferenceInitializationNode extends StatementNode {

    private final FrameSlot frameSlot;
    private final int parameterIndex;

    public ReferenceInitializationNode(FrameSlot frameSlot, int parameterIndex) {
        this.frameSlot = frameSlot;
        this.parameterIndex = parameterIndex + 1; // +1 because first parameter is always the virtual frame
    }

    /**
     * The reference object is passed in arguments in the virtual frame.
     * After reading the reference objects it is assigned to the Object slot from the frame of the actual function
     */
    @Override
    public void executeVoid(VirtualFrame frame) {
        Reference reference = (Reference)frame.getArguments()[parameterIndex];
        frame.setObject(this.frameSlot, reference);
    }
}
