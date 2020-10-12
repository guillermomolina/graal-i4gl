package org.guillermomolina.i4gl.nodes.root;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.values.I4GLNull;

/**
 * This class performs two additional tasks:
 *
 * <ul>
 * <li>Lazily registration of functions on first execution. This fulfills the semantics of
 * "evaluating" source code in I4GL.</li>
 * <li>Conversion of arguments to types understood by I4GL. The I4GL source code can be evaluated from a
 * different language, i.e., the caller can be a node from a different language that uses types not
 * understood by I4GL.</li>
 * </ul>
 */
public final class I4GLEvalRootNode extends RootNode {

    private final Map<String, RootCallTarget> functions;
    @CompilationFinal private boolean registered;

    @Child private DirectCallNode mainCallNode;

    public I4GLEvalRootNode(I4GLLanguage language, RootCallTarget mainFunction, Map<String, RootCallTarget> functions) {
        super(language);
        this.functions = functions;
        this.mainCallNode = mainFunction != null ? DirectCallNode.create(mainFunction) : null;
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
        return "root eval";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        /* Lazy registrations of functions on first execution. */
        if (!registered) {
            /* Function registration is a slow-path operation that must not be compiled. */
            CompilerDirectives.transferToInterpreterAndInvalidate();
            lookupContextReference(I4GLLanguage.class).get().getFunctionRegistry().register(functions);
            registered = true;
        }

        if (mainCallNode == null) {
            /* The source code did not have a "main" function, so nothing to execute. */
            return I4GLNull.SINGLETON;
        } else {
            /* Conversion of arguments to types understood by I4GL. */
            Object[] arguments = frame.getArguments();
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = I4GLContext.fromForeignValue(arguments[i]);
            }
            return mainCallNode.call(arguments);
        }
    }
}
