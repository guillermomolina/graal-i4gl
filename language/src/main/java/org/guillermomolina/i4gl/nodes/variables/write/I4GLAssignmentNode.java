package org.guillermomolina.i4gl.nodes.variables.write;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;

public abstract class I4GLAssignmentNode extends I4GLStatementNode {

    public abstract void executeWithValue(VirtualFrame frame, Object value);
}
