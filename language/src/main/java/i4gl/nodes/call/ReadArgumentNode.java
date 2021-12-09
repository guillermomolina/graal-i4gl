package i4gl.nodes.call;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import i4gl.nodes.expression.ExpressionNode;
import i4gl.nodes.variables.read.ReadFromRecordNodeGen;
import i4gl.runtime.exceptions.I4GLRuntimeException;
import i4gl.runtime.types.BaseType;

/**
 * This node reads value from an record with specified identifier.
 *
 * This node uses specializations which means that it is not used directly but
 * completed node is generated by Truffle.
 * {@link ReadFromRecordNodeGen}
 */
@NodeInfo
public class ReadArgumentNode extends ExpressionNode {
    /** The argument number, i.e., the index into the array of arguments. */
    private final int index;

    public ReadArgumentNode(int index) {
        this.index = index;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (index >= args.length) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new I4GLRuntimeException("Unable to read argument number " + index + "(zero-indexed) from args: "
                    + Arrays.toString(args)
                    + ". This is most likely because the function is being called with fewer arguments than how many are defined.");
        }
        return args[index];
    }

    @Override
    public BaseType getType() {
        return null;
    }
}
