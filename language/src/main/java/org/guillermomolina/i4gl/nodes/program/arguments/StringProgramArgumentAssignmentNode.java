package org.guillermomolina.i4gl.nodes.program.arguments;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;

/**
 * Strings in our interpreter are wrapped inside {@link I4GLString} instances. To make our interpreter more user friendly
 * and make its users able to pass classic Java {@link String} as an argument to the program we implement this node. It
 * reads a {@link String} from program arguments and wraps it inside {@link I4GLString}.
 */
public class StringProgramArgumentAssignmentNode extends StatementNode {

    private final FrameSlot frameSlot;
    private final int index;

    StringProgramArgumentAssignmentNode(FrameSlot frameSlot, int index) {
        this.frameSlot = frameSlot;
        this.index = index + 1;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setObject(this.frameSlot, new I4GLString((String) frame.getArguments()[this.index]));
    }

}
