package com.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import com.guillermomolina.i4gl.runtime.exceptions.GotoException;

/**
 * Node representing goto statement. To see how the goto statements are implemented please see the programming documentation.
 */
public class I4GLGotoNode extends I4GLStatementNode {

    private final String labelIdentifier;

    public I4GLGotoNode(String labelIdentifier) {
        this.labelIdentifier = labelIdentifier;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw new GotoException(this.labelIdentifier);
    }

}
