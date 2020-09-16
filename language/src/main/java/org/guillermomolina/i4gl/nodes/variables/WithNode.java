package org.guillermomolina.i4gl.nodes.variables;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.runtime.customvalues.RecordValue;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

import java.util.List;

/**
 * Representation of I4GL's with statement. It steps into frame of provided record values and executes the inner
 * statements.
 */
public class WithNode extends StatementNode {

    private final List<FrameSlot> recordSlots;

    @Child private StatementNode innerStatement;

    public WithNode(List<FrameSlot> recordSlots, StatementNode innerStatement) {
        this.recordSlots = recordSlots;
        this.innerStatement = innerStatement;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try {
            for (FrameSlot recordSlot : this.recordSlots) {
                RecordValue record = (RecordValue) frame.getObject(recordSlot);
                frame = record.getFrame();
            }

            innerStatement.executeVoid(frame);
        } catch (FrameSlotTypeException e) {
            throw new I4GLRuntimeException("Unexpected accessing of non record type");
        }
    }
}
