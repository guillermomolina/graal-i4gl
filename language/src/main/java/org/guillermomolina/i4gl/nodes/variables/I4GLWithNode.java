package org.guillermomolina.i4gl.nodes.variables;

import java.util.List;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;

/**
 * Representation of I4GL's with statement. It steps into frame of provided record values and executes the inner
 * statements.
 */
public class I4GLWithNode extends I4GLStatementNode {

    @SuppressWarnings("unused") final List<FrameSlot> recordSlots;

    @Child private I4GLStatementNode innerStatement;

    public I4GLWithNode(List<FrameSlot> recordSlots, I4GLStatementNode innerStatement) {
        this.recordSlots = recordSlots;
        this.innerStatement = innerStatement;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw new NotImplementedException();
        /*try {
            for (FrameSlot recordSlot : this.recordSlots) {
                RecordValue record = (RecordValue) frame.getObject(recordSlot);
                frame = record.getFrame();
            }

            innerStatement.executeVoid(frame);
        } catch (FrameSlotTypeException e) {
            throw new I4GLRuntimeException("Unexpected accessing of non record type");
        }*/
    }
}
