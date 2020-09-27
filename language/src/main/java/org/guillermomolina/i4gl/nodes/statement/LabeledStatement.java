package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.runtime.exceptions.GotoException;

/**
 * Node representing a labeled statement. In addition to {@link I4GLStatementNode} it contains identifier of a label which
 * prefixes it.
 */
public class LabeledStatement extends I4GLStatementNode {

    @Child private I4GLStatementNode statement;
    private final String label;

    public LabeledStatement(I4GLStatementNode statement, String label) {
        this.statement = statement;
        this.label = label;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        while (true) {
            try {
                statement.executeVoid(frame);
                break;
            } catch (GotoException e) {
                if (!e.getLabelIdentifier().equals(this.label)) {
                    throw e;
                }
            }
        }
    }

    public String getLabel() {
        return this.label;
    }

}
