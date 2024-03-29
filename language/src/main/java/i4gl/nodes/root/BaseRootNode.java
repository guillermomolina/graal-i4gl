package i4gl.nodes.root;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

import i4gl.I4GLLanguage;
import i4gl.I4GLTypeSystem;
import i4gl.exceptions.ReturnException;
import i4gl.nodes.statement.StatementNode;
import i4gl.runtime.context.Context;
import i4gl.runtime.values.Null;

/**
 * This node represents the root node of AST of any function or main program.
 */
@TypeSystemReference(I4GLTypeSystem.class)
@NodeInfo(language = "i4gl", description = "The root of all I4GL execution trees")
public class BaseRootNode extends RootNode {
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
    protected StatementNode bodyNode;

    public BaseRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, StatementNode bodyNode,
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
        assert Context.get(this) != null;
        try {
            // Execute the function body.
            bodyNode.executeVoid(frame);
        } catch (ReturnException ex) {
            // In the interpreter, record profiling information that the function has an
            // explicit return.
            exceptionTaken.enter();
            // The exception transports the actual return value.
            return ex.getResult();
        }


        // In the interpreter, record profiling information that the function ends
        // without an explicit return.         
        nullTaken.enter();
        // Return the default null value. 
        return Null.SINGLETON;
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
