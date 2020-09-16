package org.guillermomolina.i4gl.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.GotoException;

/**
 * Node representing goto statement. To see how the goto statements are implemented please see the programming documentation.
 */
public class GotoNode extends StatementNode {

    private final String labelIdentifier;

    public GotoNode(String labelIdentifier) {
        this.labelIdentifier = labelIdentifier;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        throw new GotoException(this.labelIdentifier);
    }

}
