package i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import i4gl.I4GLLanguage;
import i4gl.nodes.expression.ExpressionNode;
import i4gl.runtime.context.I4GLContext;

@NodeInfo(language = "I4GL", description = "The root of all I4GL builtin execution trees")
public class BuiltinRootNode extends RootNode {
    /** The function body that is executed, and specialized during execution. */
    @Child private ExpressionNode bodyNode;

    /** The name of the function, for printing purposes only. */
    private final String name;

    private boolean isCloningAllowed;

    private final SourceSection sourceSection;

    public BuiltinRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, ExpressionNode bodyNode, SourceSection sourceSection, String name) {
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
        assert I4GLContext.get(this) != null;
        return bodyNode.executeGeneric(frame);
    }

    public ExpressionNode getBodyNode() {
        return bodyNode;
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
}