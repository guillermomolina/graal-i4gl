package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.runtime.customvalues.Reference;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * When a variable is passed to a subroutine as a reference, it has to be wrapped to a {@link Reference} object. This
 * node takes care of this wrapping.
 */
public class StoreReferenceArgumentNode extends ExpressionNode {

	private final FrameSlot variableSlot;

	private final TypeDescriptor typeDescriptor;

	public StoreReferenceArgumentNode(FrameSlot variableSlot, TypeDescriptor typeDescriptor) {
		this.variableSlot = variableSlot;
        this.typeDescriptor = typeDescriptor;
    }

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return new Reference(frame, this.variableSlot);
	}

	@Override
    public TypeDescriptor getType() {
	    return this.typeDescriptor;
    }

}
