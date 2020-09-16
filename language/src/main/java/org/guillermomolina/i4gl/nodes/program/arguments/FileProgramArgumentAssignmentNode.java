package org.guillermomolina.i4gl.nodes.program.arguments;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.FileValue;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * If a I4GL program receives a path to a file as an argument, and type of this argument is file, this node automatically
 * assigns this path to the file variable.
 */
public class FileProgramArgumentAssignmentNode extends StatementNode {

    private final FrameSlot frameSlot;
    private final int index;

    FileProgramArgumentAssignmentNode(FrameSlot frameSlot, int index) {
        this.frameSlot = frameSlot;
        this.index = index + 1;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try {
            FileValue file = (FileValue) frame.getObject(frameSlot);
            file.assignFilePath((String) frame.getArguments()[index]);
        } catch (FrameSlotTypeException e) {
            throw new I4GLRuntimeException("Something went wrong");
        }
    }

}
