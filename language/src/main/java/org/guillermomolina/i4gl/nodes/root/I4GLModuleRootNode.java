package org.guillermomolina.i4gl.nodes.root;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.values.I4GLNull;

public final class I4GLModuleRootNode extends RootNode {
    private final String moduleName;
    private final Map<String, RootCallTarget> functions;
    private final FrameDescriptor globalsFrameDescriptor;
    @CompilationFinal
    private boolean registered;

    @Child
    private DirectCallNode mainCallNode;

    public I4GLModuleRootNode(I4GLLanguage language, final String moduleName, Map<String, RootCallTarget> functions,
            FrameDescriptor globalsFrameDescriptor) {
        super(language);
        this.moduleName = moduleName;
        this.functions = functions;
        this.globalsFrameDescriptor = globalsFrameDescriptor;
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
        return moduleName;
    }

    @Override
    public String toString() {
        return "Module " + moduleName;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        /* Lazy registrations of functions on first execution. */
        if (!registered) {
            /* Function registration is a slow-path operation that must not be compiled. */
            CompilerDirectives.transferToInterpreterAndInvalidate();
            VirtualFrame globalsFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], globalsFrameDescriptor);
            lookupContextReference(I4GLLanguage.class).get().getFrameRegistry().put("GLOBAL", globalsFrame);
            lookupContextReference(I4GLLanguage.class).get().getFunctionRegistry().register(functions);
            registered = true;
        }

        return I4GLNull.SINGLETON;
    }
}
