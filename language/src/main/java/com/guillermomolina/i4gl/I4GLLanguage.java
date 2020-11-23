package com.guillermomolina.i4gl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.nodes.builtin.I4GLBuiltinNode;
import com.guillermomolina.i4gl.nodes.root.I4GLModuleRootNode;
import com.guillermomolina.i4gl.parser.I4GLFullParser;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.guillermomolina.i4gl.runtime.context.I4GLLanguageView;

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
    public static final String ID = "i4gl";
    public static final String MIME_TYPE = "application/x-i4gl";

    private static final TruffleLogger LOGGER = TruffleLogger.getLogger(ID, I4GLLanguage.class);

    @Override
    protected I4GLContext createContext(Env environment) {
        LOGGER.fine("Creating new I4GLContext");
        return new I4GLContext(this, environment);
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
    protected Object getLanguageView(I4GLContext context, Object value) {
        return I4GLLanguageView.forValue(value);
    }

    @Override
    protected boolean isVisible(I4GLContext context, Object value) {
        return !InteropLibrary.getFactory().getUncached(value).isNull(value);
    }

    @Override
    protected Iterable<Scope> findTopScopes(I4GLContext context) {
        return context.getTopScopes();
    }

    public static I4GLContext getCurrentContext() {
        return getCurrentContext(I4GLLanguage.class);
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
