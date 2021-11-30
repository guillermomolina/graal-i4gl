package i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import i4gl.I4GLLanguage;
import i4gl.nodes.expression.I4GLExpressionNode;
import i4gl.runtime.context.I4GLContext;

@NodeInfo(language = "I4GL", description = "The root of all I4GL builtin execution trees")
public class I4GLBuiltinRootNode extends RootNode {
    /** The function body that is executed, and specialized during execution. */
    @Child private I4GLExpressionNode bodyNode;

    /** The name of the function, for printing purposes only. */
    private final String name;

    private boolean isCloningAllowed;

    private final SourceSection sourceSection;

    public I4GLBuiltinRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, I4GLExpressionNode bodyNode, SourceSection sourceSection, String name) {
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

    public I4GLExpressionNode getBodyNode() {
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

    @Override
    public String toString() {
        return "root " + name;
    }

}