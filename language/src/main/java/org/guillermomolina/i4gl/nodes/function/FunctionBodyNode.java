package org.guillermomolina.i4gl.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.ReturnException;

/**
 * Node representing body of a function. It contains a body node which is executed. The returning value is stored in a
 * write only variable of the same name as the function. The value of this variable is returned after the body node is
 * executed.
 *
 */
public class FunctionBodyNode extends ExpressionNode {

	@Child
	private StatementNode bodyNode;

    /**
     * Profiling information, collected by the interpreter, capturing whether the function had an
     * {@link SLReturnNode explicit return statement}. This allows the compiler to generate better
     * code.
     */
    private final BranchProfile exceptionTaken = BranchProfile.create();
    private final BranchProfile nullTaken = BranchProfile.create();

	public FunctionBodyNode(StatementNode bodyNode) {
		this.bodyNode = bodyNode;
	}

	@Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            /* Execute the function body. */
            bodyNode.executeVoid(frame);
        } catch (ReturnException ex) {
            /*
             * In the interpreter, record profiling information that the function has an explicit
             * return.
             */
            exceptionTaken.enter();
            /* The exception transports the actual return value. */
            return ex.getResult();
        }

        /*
         * In the interpreter, record profiling information that the function ends without an
         * explicit return.
         */
        nullTaken.enter();
        /* Return the default null value. */
        return 0;
    }

    @Override
    public TypeDescriptor getType() {
        return null;
    }

}
