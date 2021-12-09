package i4gl.nodes.root;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;

import i4gl.I4GLLanguage;
import i4gl.runtime.context.Context;

/**
 *
 * Conversion of arguments to types understood by I4GL. The I4GL source code
 * can be evaluated from a different language, i.e., the caller can be a node
 * from a different language that uses types not understood by I4GL.
 */
public final class InteropRootNode extends RootNode {

    @CompilationFinal
    private boolean registered;

    @Child
    private DirectCallNode mainCallNode;

    public InteropRootNode(I4GLLanguage language, RootCallTarget function) {
        super(language);
        this.mainCallNode = function != null ? DirectCallNode.create(function) : null;
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    protected boolean isInstrumentable() {
        return false;
    }

    @Override
    public String getName() {
        return "interop eval";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        // Conversion of arguments to types understood by I4GL.
        Object[] arguments = frame.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = Context.fromForeignValue(arguments[i]);
        }
        return mainCallNode.call(arguments);
    }
}
