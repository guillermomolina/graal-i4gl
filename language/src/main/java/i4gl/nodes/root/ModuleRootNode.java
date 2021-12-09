package i4gl.nodes.root;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;

import i4gl.I4GLLanguage;
import i4gl.runtime.context.Context;
import i4gl.runtime.values.Null;

public final class ModuleRootNode extends RootNode {
    private final String moduleName;
    private final Map<String, RootCallTarget> functions;
    private final FrameDescriptor globalsFrameDescriptor;
    private final FrameDescriptor moduleFrameDescriptor;
    @CompilationFinal
    private boolean registered;

    @Child
    private DirectCallNode mainCallNode;

    public ModuleRootNode(I4GLLanguage language, final String moduleName, Map<String, RootCallTarget> functions,
    FrameDescriptor globalsFrameDescriptor,
    FrameDescriptor moduleFrameDescriptor) {
        super(language);
        this.moduleName = moduleName;
        this.functions = functions;
        this.globalsFrameDescriptor = globalsFrameDescriptor;
        this.moduleFrameDescriptor = moduleFrameDescriptor;
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
            Context context = Context.get(this);
            final VirtualFrame globalsFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], globalsFrameDescriptor);
            context.addModuleFrame("GLOBAL", globalsFrame);
            final VirtualFrame moduleFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], moduleFrameDescriptor);
            context.addModuleFrame(moduleName, moduleFrame);
            context.getFunctionRegistry().register(functions);
            registered = true;
        }

        return Null.SINGLETON;
    }
}
