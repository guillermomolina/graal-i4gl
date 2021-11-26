package com.guillermomolina.i4gl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.nodes.builtin.I4GLBuiltinNode;
import com.guillermomolina.i4gl.nodes.root.I4GLModuleRootNode;
import com.guillermomolina.i4gl.nodes.root.I4GLUndefinedFunctionRootNode;
import com.guillermomolina.i4gl.parser.I4GLFullParser;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.guillermomolina.i4gl.runtime.context.I4GLLanguageView;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

/**
 * Representation of our I4GL guest language for Truffle VM. Thanks to the
 * TruffleLanguage.Registration annotation we register this class so that
 * Truffle's PolyglotEngine will use our language.
 */
@TruffleLanguage.Registration(id = I4GLLanguage.ID, name = "I4GLLanguage", defaultMimeType = I4GLLanguage.MIME_TYPE, characterMimeTypes = I4GLLanguage.MIME_TYPE, contextPolicy = ContextPolicy.SHARED, fileTypeDetectors = I4GLFileDetector.class)
@ProvidedTags({ StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class,
        StandardTags.RootBodyTag.class, StandardTags.ExpressionTag.class, DebuggerTags.AlwaysHalt.class,
        StandardTags.ReadVariableTag.class, StandardTags.WriteVariableTag.class })
public final class I4GLLanguage extends TruffleLanguage<I4GLContext> {
    public static volatile int counter;

    public static final String ID = "i4gl";
    public static final String MIME_TYPE = "application/x-i4gl";

    private static final TruffleLogger LOGGER = TruffleLogger.getLogger(ID, I4GLLanguage.class);

    private final Assumption singleContext = Truffle.getRuntime().createAssumption("Single I4GL context.");

    private final Map<String, RootCallTarget> undefinedFunctions = new ConcurrentHashMap<>();


    public I4GLLanguage() {
        counter++;
    }

    @Override
    protected I4GLContext createContext(Env environment) {
        LOGGER.fine("Creating new I4GLContext");
        return new I4GLContext(this, environment);
    }


    @Override
    protected boolean patchContext(I4GLContext context, Env newEnv) {
        context.patchContext(newEnv);
        return true;
    }

    public RootCallTarget getOrCreateUndefinedFunction(String name) {
        RootCallTarget target = undefinedFunctions.get(name);
        if (target == null) {
            target = Truffle.getRuntime().createCallTarget(new I4GLUndefinedFunctionRootNode(this, name));
            RootCallTarget other = undefinedFunctions.putIfAbsent(name, target);
            if (other != null) {
                target = other;
            }
        }
        return target;
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

    /**
     * Gets source from the request, parses it and return call target that, if
     * called, executes given script in I4GL language.
     * 
     * @param request parsing request
     * @throws I4GLParseException the source cannot be parsed
     */
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        LOGGER.fine("Received parse request");
        Source source = request.getSource();
        if (!request.getArgumentNames().isEmpty()) {
            throw new NotImplementedException();
        }
        LOGGER.log(Level.FINE, "Start parsing {0}", source.getPath());
        final I4GLFullParser parser = new I4GLFullParser(this, source);
        RootNode moduleRootNode = new I4GLModuleRootNode(this, parser.getModuleName(), parser.getAllFunctions(),
                parser.getGlobalsFrameDescriptor(), parser.getModuleFrameDescriptor());
        LOGGER.log(Level.FINE, "Finish parsing {0}", source.getPath());
        return Truffle.getRuntime().createCallTarget(moduleRootNode);
    }

    @Override
    protected void initializeMultipleContexts() {
        singleContext.invalidate();
    }

    public boolean isSingleContext() {
        return singleContext.isValid();
    }

    @Override
    protected Object getLanguageView(I4GLContext context, Object value) {
        return I4GLLanguageView.create(value);
    }

    @Override
    protected boolean isVisible(I4GLContext context, Object value) {
        return !InteropLibrary.getFactory().getUncached(value).isNull(value);
    }

    @Override
    protected Object getScope(I4GLContext context) {
        return context.getFunctionRegistry().getFunctionsObject();
    }
    private static final LanguageReference<I4GLLanguage> REFERENCE = LanguageReference.create(I4GLLanguage.class);

    public static I4GLLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    private static final List<NodeFactory<? extends I4GLBuiltinNode>> EXTERNAL_BUILTINS = Collections
            .synchronizedList(new ArrayList<>());

    public static void installBuiltin(NodeFactory<? extends I4GLBuiltinNode> builtin) {
        EXTERNAL_BUILTINS.add(builtin);
    }

    @TruffleBoundary
    public static TruffleLogger getLogger(Class<?> clazz) {
        return TruffleLogger.getLogger(ID, clazz);
    }
}
