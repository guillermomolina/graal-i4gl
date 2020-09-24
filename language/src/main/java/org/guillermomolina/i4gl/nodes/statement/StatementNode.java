package org.guillermomolina.i4gl.nodes.statement;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

/**
 * This class is an abstract class for each node that represent a statement.
 */
@NodeInfo(language = "i4gl", description = "The abstract base node for all I4GL statements")
@GenerateWrapper
public abstract class StatementNode extends Node implements InstrumentableNode {
    private static final int NO_SOURCE = -1;
    private static final int UNAVAILABLE_SOURCE = -2;

    private int sourceCharIndex = NO_SOURCE;
    private int sourceLength;

    private boolean hasStatementTag;
    private boolean hasRootTag;

    /*
     * The creation of source section can be implemented lazily by looking up the root node source
     * and then creating the source section object using the indices stored in the node. This avoids
     * the eager creation of source section objects during parsing and creates them only when they
     * are needed. Alternatively, if the language uses source sections to implement language
     * semantics, then it might be more efficient to eagerly create source sections and store it in
     * the AST.
     *
     * For more details see {@link InstrumentableNode}.
     */
    @Override
    @TruffleBoundary
    public final SourceSection getSourceSection() {
        if (sourceCharIndex == NO_SOURCE) {
            // AST node without source
            return null;
        }
        RootNode rootNode = getRootNode();
        if (rootNode == null) {
            // not yet adopted yet
            return null;
        }
        SourceSection rootSourceSection = rootNode.getSourceSection();
        if (rootSourceSection == null) {
            return null;
        }
        Source source = rootSourceSection.getSource();
        if (sourceCharIndex == UNAVAILABLE_SOURCE) {
            if (hasRootTag && !rootSourceSection.isAvailable()) {
                return rootSourceSection;
            } else {
                return source.createUnavailableSection();
            }
        } else {
            return source.createSection(sourceCharIndex, sourceLength);
        }
    }

    public final boolean hasSource() {
        return sourceCharIndex != NO_SOURCE;
    }

    public final boolean isInstrumentable() {
        return hasSource();
    }

    public final int getSourceCharIndex() {
        return sourceCharIndex;
    }

    public final int getSourceEndIndex() {
        return sourceCharIndex + sourceLength;
    }

    public final int getSourceLength() {
        return sourceLength;
    }

    // invoked by the parser to set the source
    public final void setSourceSection(int charIndex, int length) {
        assert sourceCharIndex == NO_SOURCE : "source must only be set once";
        if (charIndex < 0) {
            throw new IllegalArgumentException("charIndex < 0");
        } else if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.sourceCharIndex = charIndex;
        this.sourceLength = length;
    }

    public final void setUnavailableSourceSection() {
        assert sourceCharIndex == NO_SOURCE : "source must only be set once";
        this.sourceCharIndex = UNAVAILABLE_SOURCE;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        if (tag == StandardTags.StatementTag.class) {
            return hasStatementTag;
        } else if (tag == StandardTags.RootTag.class || tag == StandardTags.RootBodyTag.class) {
            return hasRootTag;
        }
        return false;
    }

    public WrapperNode createWrapper(ProbeNode probe) {
        return new StatementNodeWrapper(this, probe);
    }

    /**
     * Marks this node as being a {@link StandardTags.StatementTag} for instrumentation purposes.
     */
    public final void addStatementTag() {
        hasStatementTag = true;
    }

    /**
     * Marks this node as being a {@link StandardTags.RootTag} and {@link StandardTags.RootBodyTag}
     * for instrumentation purposes.
     */
    public final void addRootTag() {
        hasRootTag = true;
    }

    @Override
    public String toString() {
        return formatSourceSection(this);
    }

    /**
     * Formats a source section of a node in human readable form. If no source section could be
     * found it looks up the parent hierarchy until it finds a source section. Nodes where this was
     * required append a <code>'~'</code> at the end.
     *
     * @param node the node to format.
     * @return a formatted source section string
     */
    public static String formatSourceSection(Node node) {
        if (node == null) {
            return "<unknown>";
        }
        SourceSection section = node.getSourceSection();
        boolean estimated = false;
        if (section == null) {
            section = node.getEncapsulatingSourceSection();
            estimated = true;
        }

        if (section == null || section.getSource() == null) {
            return "<unknown source>";
        } else {
            String sourceName = section.getSource().getName();
            int startLine = section.getStartLine();
            return String.format("%s:%d%s", sourceName, startLine, estimated ? "~" : "");
        }
    }

    /**
     * This method is used for compile time type checking. Each node that must be verified that it got children
     * nodes of right types (mostly operation nodes) implements its type checking in this method.
     * @return true if the children nodes are of the required type, false otherwise
     */
    public boolean verifyChildrenNodeTypes() {
        return true;
    }

    /**
     * Executes the statement node without returning any value
     * @param frame current frame
     */
	public abstract void executeVoid(VirtualFrame frame);

	protected VirtualFrame getFrameContainingSlot(VirtualFrame currentFrame, FrameSlot slot) {
		if(frameContainsSlot(currentFrame, slot)) {
			return currentFrame;
		}
		
		while(currentFrame.getArguments().length > 0) {
			currentFrame = (VirtualFrame)currentFrame.getArguments()[0];
			if(frameContainsSlot(currentFrame, slot)) {
				return currentFrame;
			}
		}
		
		return null;
	}

    /**
     * Gets the number of required jumps to the parent frame to get frame containing specified slot. This function is
     * used for optimization. If we have some node that uses frame slot which is not from its frame then first time it
     * is executed it has to look in which frame the slot belongs, remember the number of jumps and each next time it is
     * executed it directly jumps by that number and retrieves the slot. This is thanks to the fact that the number of jumps
     * cannot change.
     * @param currentFrame the initial frame
     * @param slot the queried slot
     * @return the number of jumps to parent frame to find the queried slot
     */
    protected int getJumpsToFrame(VirtualFrame currentFrame, FrameSlot slot) {
	    int result = 0;
        if (frameContainsSlot(currentFrame, slot)) {
            return result;
        }

        while (currentFrame.getArguments().length > 0) {
            currentFrame = (VirtualFrame) currentFrame.getArguments()[0];
            ++result;
            if (frameContainsSlot(currentFrame, slot)) {
                return result;
            }
        }

        return -1;
    }


    /**
     * Checks whether specified frame contains specified slot.
     * @param frame the frame
     * @param slot the slot
     */
    private boolean frameContainsSlot(VirtualFrame frame, FrameSlot slot) {
		return frame.getFrameDescriptor().getSlots().contains(slot);
	}

}
