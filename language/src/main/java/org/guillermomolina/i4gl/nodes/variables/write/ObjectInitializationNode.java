package org.guillermomolina.i4gl.nodes.variables.write;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;


/**
 * Initialization node for generic type variables. It looks for the variable the current frame.
 */
public final class ObjectInitializationNode extends I4GLStatementNode {

	protected final FrameSlot slot;
	protected final Object value;
	
	public ObjectInitializationNode(FrameSlot slot, Object value) {
		this.slot = slot;
		this.value = value;
	}

    @Override
    public void executeVoid(VirtualFrame frame) {
        frame.setObject(slot, value);
    }

}

