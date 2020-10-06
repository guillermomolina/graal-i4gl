package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.I4GLTypes;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.NullValue;
import org.guillermomolina.i4gl.runtime.exceptions.ReturnException;

/**
 * This node represents the root node of AST of any function or main program.
 */
@TypeSystemReference(I4GLTypes.class)
@NodeInfo(language = "i4gl", description = "The root of all I4GL execution trees")
public class I4GLRootNode extends RootNode {
    /**
     * Profiling information, collected by the interpreter, capturing whether the
     * function had an {@link SLReturnNode explicit return statement}. This allows
     * the compiler to generate better code.
     */
    private final BranchProfile exceptionTaken = BranchProfile.create();
    private final BranchProfile nullTaken = BranchProfile.create();

    /** The name of the function, for printing purposes only. */
    private final String name;

    private boolean isCloningAllowed;

    private final SourceSection sourceSection;

    @Child
    protected I4GLStatementNode bodyNode;

    public I4GLRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, I4GLStatementNode bodyNode,
            SourceSection sourceSection, String name) {
        super(language, frameDescriptor);
        this.bodyNode = bodyNode;
        this.name = name;
        this.sourceSection = sourceSection;
    }

    @Override
    public SourceSection getSourceSection() {
        return sourceSection;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        assert lookupContextReference(I4GLLanguage.class).get() != null;
        try {
            /* Execute the function body. */
            bodyNode.executeVoid(frame);
        } catch (ReturnException ex) {
            /*
             * In the interpreter, record profiling information that the function has an
             * explicit return.
             */
            exceptionTaken.enter();
            /* The exception transports the actual return value. */
            return ex.getResult();
        }

        /*
         * In the interpreter, record profiling information that the function ends
         * without an explicit return.
         */
        nullTaken.enter();
        /* Return the default null value. */
        return NullValue.SINGLETON;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setCloningAllowed(boolean isCloningAllowed) {
        this.isCloningAllowed = isCloningAllowed;
    }

    @Override
    public boolean isCloningAllowed() {
        return isCloningAllowed;
    }

    @Override
    public String toString() {
        return "root " + name;
    }
}
