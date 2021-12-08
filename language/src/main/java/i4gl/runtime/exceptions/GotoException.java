package i4gl.runtime.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * Exception is thrown when I4GL's goto statement is executed. It is caught inside a {@link i4gl.nodes.statement.LabeledStatement}
 * or {@link i4gl.nodes.statement.ExtendedBlockNode}.
 */
public class GotoException extends ControlFlowException {

    /**
     *
     */
    private static final long serialVersionUID = -8898162710266544973L;
    private final String labelIdentifier;

    public GotoException(String labelIdentifier) {
        this.labelIdentifier = labelIdentifier;
    }

    public String getLabelIdentifier() {
        return labelIdentifier;
    }
}
