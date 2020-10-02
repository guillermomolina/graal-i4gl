package org.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.dsl.NodeChild;

/**
 * Base node for each binary expression node. It also contains functions for
 * static type checking.
 */
@NodeChild("leftNode")
@NodeChild("rightNode")
public abstract class I4GLBinaryExpressionNode extends I4GLExpressionNode {
}
