package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.profiles.BranchProfile;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.I4GLTypes;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.HaltException;
import org.guillermomolina.i4gl.runtime.exceptions.ReturnException;

/**
 * This node represents the root node of AST of any function or main program.
 */
@TypeSystemReference(I4GLTypes.class)
public class I4GLRootNode extends RootNode {
    /**
     * Profiling information, collected by the interpreter, capturing whether the function had an
     * {@link SLReturnNode explicit return statement}. This allows the compiler to generate better
     * code.
     */
    private final BranchProfile exceptionTaken = BranchProfile.create();
    private final BranchProfile nullTaken = BranchProfile.create();

	@Child
	protected StatementNode bodyNode;

	public I4GLRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, StatementNode bodyNode) {
		super(language, frameDescriptor);
		this.bodyNode = bodyNode;
	}

	@Override
    public Object execute(VirtualFrame frame) {
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
        } catch (HaltException e) {
            exceptionTaken.enter(); 

            /* The exception transports the actual return value. */
            return e.getExitCode();
        }

        /*
         * In the interpreter, record profiling information that the function ends without an
         * explicit return.
         */
        nullTaken.enter();
        /* Return the default null value. */
        return 0;
    }

}
