package com.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import com.guillermomolina.i4gl.I4GLLanguage;
import com.guillermomolina.i4gl.nodes.statement.I4GLStatementNode;
import com.guillermomolina.i4gl.runtime.exceptions.HaltException;

public class I4GLMainRootNode extends I4GLRootNode {
    public I4GLMainRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, I4GLStatementNode bodyNode,
    SourceSection sourceSection, String name) {
        super(language, frameDescriptor, bodyNode, sourceSection, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            Object result = super.execute(frame);
            if(result instanceof Object[]) {
                return ((Object[])result)[0];
            }
            return result;
        } catch (HaltException e) {
            return e.getExitCode();
        }
    }    
}
