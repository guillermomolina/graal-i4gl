package org.guillermomolina.i4gl.nodes.arithmetic;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.I4GLBinaryExpressionNode;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Node representing plus operation. For numeric arguments it is addition and for set arguments it represents union.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link AddNodeGen}
 */
@NodeInfo(shortName = "+")
public abstract class I4GLAddNode extends I4GLBinaryExpressionNode {

    @Specialization(rewriteOn = ArithmeticException.class)
    protected int add(int left, int right) {
        return Math.addExact(left, right);
    }

    @Specialization
    @TruffleBoundary
    protected long add(long left, long right) {
        return Math.addExact(left, right);
    }

    @Fallback
    protected Object typeError(Object left, Object right) {
        throw new I4GLRuntimeException("Type error doing: " + left + " + " + right);
    }

    @Override
    public TypeDescriptor getType() {
        throw new NotImplementedException();
    }
}
