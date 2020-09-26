package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.ReturnValue;
import org.guillermomolina.i4gl.runtime.exceptions.HaltException;

public class MainFunctionRootNode extends I4GLRootNode {
    public MainFunctionRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, StatementNode bodyNode,
    SourceSection sourceSection, String name) {
        super(language, frameDescriptor, bodyNode, sourceSection, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            ReturnValue result = (ReturnValue) super.execute(frame);
            return result.getValueAt(0);
        } catch (HaltException e) {
            return e.getExitCode();
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }    
}
