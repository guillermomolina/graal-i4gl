package org.guillermomolina.i4gl.nodes.call;

import java.util.Arrays;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.I4GLExpressionNode;
import org.guillermomolina.i4gl.runtime.types.I4GLType;

/**
 * This node is used to read all arguments from actual frame and return them. It is used in {@link org.guillermomolina.i4gl.nodes.builtin.io.WriteBuiltinNode}
 * and {@link org.guillermomolina.i4gl.nodes.builtin.io.ReadBuiltinNode} because these function have variable
 * number of arguments.
 *
 * {@link I4GLReadArgumentNode}
 */
public class I4GLReadAllArgumentsNode extends I4GLExpressionNode {

	@Override
	public Object[] executeGeneric(VirtualFrame frame) {
		Object[] args = frame.getArguments();
		return Arrays.copyOfRange(args, 1, args.length);
	}

    @Override
    public I4GLType getType() {
        return null;
    }
}
