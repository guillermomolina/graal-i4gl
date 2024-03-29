package i4gl.runtime.context;

import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.Source;

import i4gl.I4GLLanguage;
import i4gl.exceptions.I4GLRuntimeException;
import i4gl.nodes.builtin.BuiltinNode;
import i4gl.nodes.builtin.LengthBuiltinNodeFactory;
import i4gl.nodes.builtin.fgl.FGLGetEnvBuiltinNodeFactory;
import i4gl.nodes.builtin.icgi.ICGIDecodeBuiltinNodeFactory;
import i4gl.nodes.builtin.icgi.ICGIEncodeBuiltinNodeFactory;
import i4gl.nodes.builtin.icgi.ICGIFreeBuiltinNodeFactory;
import i4gl.nodes.builtin.icgi.ICGIGetValueBuiltinNodeFactory;
import i4gl.nodes.builtin.icgi.ICGIMimeTypeBuiltinNodeFactory;
import i4gl.nodes.builtin.icgi.ICGIStartBuiltinNodeFactory;
import i4gl.nodes.variables.read.ReadLocalVariableNode;
import i4gl.nodes.variables.read.ReadLocalVariableNodeGen;
import i4gl.runtime.types.complex.SqlcaType;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.Sqlca;

public final class Context {

    @CompilationFinal
    private Env env;
    private final I4GLLanguage language;
    private final BufferedReader input;
    private final PrintWriter output;
    private final FunctionRegistry functionRegistry;
    private final Map<String, VirtualFrame> frameRegistry;
    private final AllocationReporter allocationReporter;

    public Context(I4GLLanguage language, TruffleLanguage.Env env,
            List<NodeFactory<? extends BuiltinNode>> externalBuiltins) {
        this.env = env;
        this.language = language;
        this.input = new BufferedReader(new InputStreamReader(env.in()));
        this.output = new PrintWriter(env.out(), true);
        this.functionRegistry = new FunctionRegistry(language);
        this.frameRegistry = new HashMap<>();
        this.allocationReporter = env.lookup(AllocationReporter.class);
        installBuiltins();
        for (NodeFactory<? extends BuiltinNode> builtin : externalBuiltins) {
            installBuiltin(builtin);
        }
    }

    public void installBuiltin(NodeFactory<? extends BuiltinNode> factory) {
        /* Register the builtin function in our function registry. */
        RootCallTarget target = language.lookupBuiltin(factory);
        String rootName = target.getRootNode().getName();
        getFunctionRegistry().register(rootName, target);
    }

    /**
     * Patches the {@link Context} to use a new {@link Env}. The method is
     * called during the native image execution as a consequence of
     * {@link Context#create(java.lang.String...)}.
     *
     * @param newEnv the new {@link Env} to use.
     * @see TruffleLanguage#patchContext(Object, Env)
     */
    public void patchContext(Env newEnv) {
        this.env = newEnv;
    }

    /**
     * Return the current Truffle environment.
     */
    public Env getEnv() {
        return env;
    }

    /**
     * Returns the default input, i.e., the source for the
     * {@link I4GLReadlnBuiltin}. To allow unit testing, we do not use
     * {@link System#in} directly.
     */
    public BufferedReader getInput() {
        return input;
    }

    /**
     * The default default, i.e., the output for the {@link I4GLPrintlnBuiltin}. To
     * allow unit testing, we do not use {@link System#out} directly.
     */
    public PrintWriter getOutput() {
        return output;
    }

    /**
     * Returns the registry of all functions that are currently defined.
     */
    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    public VirtualFrame getModuleFrame(final String moduleName) {
        return frameRegistry.get(moduleName);
    }

    public VirtualFrame addModuleFrame(final String moduleName, final VirtualFrame frame) {
        return frameRegistry.put(moduleName, frame);
    }

    public void addGlobalVariables(final VirtualFrame frame) {
        FrameSlot slot = frame.getFrameDescriptor().findFrameSlot("sqlca");
        frame.getFrameDescriptor().setFrameSlotKind(slot, FrameSlotKind.Object);
        frame.setObject(slot, Sqlca.SINGLETON);
    }

    @TruffleBoundary
    public Sqlca getSqlcaGlobalVariable() {
        VirtualFrame frame = frameRegistry.get("GLOBAL");
        Sqlca sqlca = null;
        if (frame != null) {
            FrameSlot slot = frame.getFrameDescriptor().findFrameSlot("sqlca");
            ReadLocalVariableNode readSqlcaNode = ReadLocalVariableNodeGen.create(slot, SqlcaType.SINGLETON);
            Object value = readSqlcaNode.executeGeneric(frame);
            if (value == null || value == Null.SINGLETON || !(value instanceof Sqlca)) {
                throw new I4GLRuntimeException("sqlca not initialized");
            }
            sqlca = (Sqlca) value;
        }
        return sqlca;
    }

    @TruffleBoundary
    private TruffleObject getModuleVariables(String moduleName) {
        VirtualFrame frame = frameRegistry.get(moduleName);
        final Variables vars = new Variables();
        if (frame != null) {
            for (FrameSlot slot : frame.getFrameDescriptor().getSlots()) {
                Object value = frame.getValue(slot);
                if (value == null) {
                    value = Null.SINGLETON;
                }
                vars.variables.put((String) slot.getIdentifier(), value);
            }
        }
        return vars;
    }

    /**
     * Adds all builtin functions to the {@link FunctionRegistry}. This method
     * lists all
     * {@link BuiltinNode builtin implementation classes}.
     */
    private void installBuiltins() {
        installBuiltin(LengthBuiltinNodeFactory.getInstance());
        installBuiltin(ICGIDecodeBuiltinNodeFactory.getInstance());
        installBuiltin(FGLGetEnvBuiltinNodeFactory.getInstance());
        installBuiltin(ICGIEncodeBuiltinNodeFactory.getInstance());
        installBuiltin(ICGIFreeBuiltinNodeFactory.getInstance());
        installBuiltin(ICGIGetValueBuiltinNodeFactory.getInstance());
        installBuiltin(ICGIMimeTypeBuiltinNodeFactory.getInstance());
        installBuiltin(ICGIStartBuiltinNodeFactory.getInstance());
    }

    public static NodeInfo lookupNodeInfo(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        NodeInfo info = clazz.getAnnotation(NodeInfo.class);
        if (info != null) {
            return info;
        } else {
            return lookupNodeInfo(clazz.getSuperclass());
        }
    }

    /*
     * Methods for object creation / object property access.
     */
    public AllocationReporter getAllocationReporter() {
        return allocationReporter;
    }

    /*
     * Methods for language interoperability.
     */

    public static Object fromForeignValue(Object a) {
        if (a instanceof Long || a instanceof Integer || a instanceof String || a instanceof Boolean) {
            return a;
        } else if (a instanceof Character) {
            return fromForeignCharacter((Character) a);
        } else if (a instanceof Number) {
            return fromForeignNumber(a);
        } else if (a instanceof TruffleObject) {
            return a;
        } else if (a instanceof Context) {
            return a;
        }
        throw shouldNotReachHere("Value is not a truffle value.");
    }

    @TruffleBoundary
    private static long fromForeignNumber(Object a) {
        return ((Number) a).longValue();
    }

    @TruffleBoundary
    private static String fromForeignCharacter(char c) {
        return String.valueOf(c);
    }

    public CallTarget parse(Source source) {
        return env.parsePublic(source);
    }

    /**
     * Returns an object that contains bindings that were exported across all used
     * languages. To read or write from this object the {@link TruffleObject
     * interop} API can be used.
     */
    public TruffleObject getPolyglotBindings() {
        return (TruffleObject) env.getPolyglotBindings();
    }

    private static final ContextReference<Context> REFERENCE = ContextReference.create(I4GLLanguage.class);

    public static Context get(Node node) {
        return REFERENCE.get(node);
    }

}
