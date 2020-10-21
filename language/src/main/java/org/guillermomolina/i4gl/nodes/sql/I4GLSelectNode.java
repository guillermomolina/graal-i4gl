package org.guillermomolina.i4gl.nodes.sql;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseException;
import org.guillermomolina.i4gl.runtime.exceptions.IncorrectNumberOfReturnValuesException;
import org.guillermomolina.i4gl.runtime.values.I4GLCursor;
import org.guillermomolina.i4gl.runtime.values.I4GLDatabase;

public class I4GLSelectNode extends I4GLStatementNode {
    @Child
    private I4GLExpressionNode databaseVariableNode;
    @Child
    private InteropLibrary interop;
    private  final String sql;
    private final FrameSlot[] resultSlots;

    public I4GLSelectNode(final I4GLExpressionNode databaseVariableNode, final String sql, FrameSlot[] resultSlots) {
        this.databaseVariableNode = databaseVariableNode;
        this.interop = InteropLibrary.getFactory().createDispatched(3);
        this.sql = sql;
        this.resultSlots = resultSlots;
    }

    public void evaluateResult(VirtualFrame frame, Object[] returnValue) {
        if (resultSlots.length != 0) {
            if (returnValue.length != resultSlots.length) {
                throw new IncorrectNumberOfReturnValuesException(resultSlots.length, returnValue.length);
            }
            for (int index = 0; index < resultSlots.length; index++) {
                final Object result = returnValue[index];
                final FrameSlot slot = resultSlots[index];
                final FrameSlotKind slotKind = frame.getFrameDescriptor().getFrameSlotKind(slot);
                switch (slotKind) {
                    case Int:
                        frame.setInt(slot, (int) result);
                        break;
                    case Long:
                        frame.setLong(slot, (long) result);
                        break;
                    case Float:
                        frame.setFloat(slot, (float) result);
                        break;
                    case Double:
                        frame.setDouble(slot, (double) result);
                        break;
                    case Object:
                        frame.setObject(slot, result);
                        break;
                    default:
                }
            }    
        }
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        final I4GLDatabase database = (I4GLDatabase) databaseVariableNode.executeGeneric(frame);
        final I4GLCursor cursor = new I4GLCursor(database, sql);
        cursor.start();

        Object[] result = null;
        if(cursor.next()) {
            result = cursor.getRow();
        }
        if (cursor.next()) {
            final String query = sql.replace("\n", "").replace("\r", "").replace("\t", "");
            throw new DatabaseException("The query \"" + query + "\" has not returned exactly one row.");
        }
        cursor.end();
        
        if(result != null) {
            evaluateResult(frame, result);
        }
    }
}