package org.guillermomolina.i4gl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Scope;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.nodes.I4GLLexicalScope;
import org.guillermomolina.i4gl.nodes.builtin.I4GLBuiltinNode;
import org.guillermomolina.i4gl.nodes.root.I4GLEvalRootNode;
import org.guillermomolina.i4gl.parser.I4GLParserFactory;
import org.guillermomolina.i4gl.runtime.I4GLLanguageView;

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

    // To make the linter happy, remove it
    public static final I4GLLanguage INSTANCE = null;

    @Override
    protected I4GLContext createContext(Env environment) {
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
        Source source = request.getSource();
        if (!request.getArgumentNames().isEmpty()) {
            throw new NotImplementedException();
        }
        Map<String, RootCallTarget> functions = I4GLParserFactory.parseI4GL(this, source);
        RootCallTarget main = functions.get("MAIN");
        RootNode evalMain;
        if (main != null) {
            /*
             * We have a main function, so "evaluating" the parsed source means invoking
             * that main function. However, we need to lazily register functions into the
             * I4GLContext first, so we cannot use the original I4GLRootNode for the main
             * function. Instead, we create a new I4GLEvalRootNode that does everything we
             * need.
             */
            evalMain = new I4GLEvalRootNode(this, main, functions);
        } else {
            /*
             * Even without a main function, "evaluating" the parsed source needs to
             * register the functions into the I4GLContext.
             */
            evalMain = new I4GLEvalRootNode(this, null, functions);
        }
        return Truffle.getRuntime().createCallTarget(evalMain);
    }

    @Override
    protected Object getLanguageView(I4GLContext context, Object value) {
        return I4GLLanguageView.create(value);
    }

    /**
     * Does some thing in old style.
     *
     * @deprecated
     */
    @Deprecated
    @Override
    protected Object findExportedSymbol(I4GLContext context, String globalName, boolean onlyExplicit) {
        return null;
    }

    @Override
    protected boolean isVisible(I4GLContext context, Object value) {
        return !InteropLibrary.getFactory().getUncached(value).isNull(value);
    }

    @Override
    public Iterable<Scope> findLocalScopes(I4GLContext context, Node node, Frame frame) {
        final I4GLLexicalScope scope = I4GLLexicalScope.createScope(node);
        return new Iterable<Scope>() {
            @Override
            public Iterator<Scope> iterator() {
                return new Iterator<Scope>() {
                    private I4GLLexicalScope previousScope;
                    private I4GLLexicalScope nextScope = scope;

                    @Override
                    public boolean hasNext() {
                        if (nextScope == null) {
                            nextScope = previousScope.findParent();
                        }
                        return nextScope != null;
                    }

                    @Override
                    public Scope next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Object functionObject = findFunctionObject();
                        Scope vscope = Scope.newBuilder(nextScope.getName(), nextScope.getVariables(frame))
                                .node(nextScope.getNode()).arguments(nextScope.getArguments(frame))
                                .rootInstance(functionObject).build();
                        previousScope = nextScope;
                        nextScope = null;
                        return vscope;
                    }

                    private Object findFunctionObject() {
                        String name = node.getRootNode().getName();
                        return context.getFunctionRegistry().getFunction(name);
                    }
                };
            }
        };
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
}
