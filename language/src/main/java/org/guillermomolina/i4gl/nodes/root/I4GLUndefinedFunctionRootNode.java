package org.guillermomolina.i4gl.nodes.root;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.exceptions.UndefinedNameException;
import org.guillermomolina.i4gl.runtime.context.I4GLFunction;

/**
 * The initial {@link RootNode} of {@link I4GLFunction functions} when they are created, i.e., when
 * they are still undefined. Executing it throws an
 * {@link I4GLUndefinedNameException#undefinedFunction exception}.
 */
public class I4GLUndefinedFunctionRootNode extends I4GLRootNode {
    public I4GLUndefinedFunctionRootNode(I4GLLanguage language, String name) {
        super(language, null, null, null, name);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        throw UndefinedNameException.undefinedFunction(null, getName());
    }
}
