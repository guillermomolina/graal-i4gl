package org.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.dsl.NodeChild;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;

/**
 * Base node for each binary expression node. It also contains functions for
 * static type checking.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class I4GLBinaryExpressionNode extends I4GLExpressionNode {

    @Override
    public I4GLTypeDescriptor getType() {
        throw new NotImplementedException();
    }
}
