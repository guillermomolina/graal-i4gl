package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import org.guillermomolina.i4gl.runtime.exceptions.HaltException;

public class I4GLMainRootNode extends I4GLRootNode {
    public I4GLMainRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, I4GLStatementNode bodyNode,
    SourceSection sourceSection, String name) {
        super(language, frameDescriptor, bodyNode, sourceSection, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            Object[] result = (Object[]) super.execute(frame);
            return result[0];
        } catch (HaltException e) {
            return e.getExitCode();
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }    
}