package i4gl.nodes.root;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

import i4gl.I4GLLanguage;
import i4gl.exceptions.HaltException;
import i4gl.nodes.statement.StatementNode;

public class MainRootNode extends BaseRootNode {
    public MainRootNode(I4GLLanguage language, FrameDescriptor frameDescriptor, StatementNode bodyNode,
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
