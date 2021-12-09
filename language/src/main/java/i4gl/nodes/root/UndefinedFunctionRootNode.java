package i4gl.nodes.root;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import i4gl.I4GLLanguage;
import i4gl.runtime.context.Function;
import i4gl.runtime.exceptions.UndefinedNameException;

/**
 * The initial {@link RootNode} of {@link Function functions} when they are created, i.e., when
 * they are still undefined. Executing it throws an
 * {@link I4GLUndefinedNameException#undefinedFunction exception}.
 */
public class UndefinedFunctionRootNode extends BaseRootNode {
    public UndefinedFunctionRootNode(I4GLLanguage language, String name) {
        super(language, null, null, null, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        throw UndefinedNameException.undefinedFunction(this, getName());
    }
}
