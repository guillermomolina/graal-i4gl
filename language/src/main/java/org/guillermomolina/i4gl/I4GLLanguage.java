package org.guillermomolina.i4gl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.parser.I4GLLexer;
import org.guillermomolina.i4gl.parser.I4GLParser;
import org.guillermomolina.i4gl.parser.I4GLVisitorImpl;
import org.guillermomolina.i4gl.parser.exceptions.BailoutErrorListener;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;

/**
 * Representation of our I4GL guest language for Truffle VM. Thanks to the
 * TruffleLanguage.Registration annotation we register this class so that
 * Truffle's PolyglotEngine will use our language.
 */
@TruffleLanguage.Registration(id = I4GLLanguage.ID, name = "I4GLLanguage", defaultMimeType = I4GLLanguage.MIME_TYPE, characterMimeTypes = I4GLLanguage.MIME_TYPE, contextPolicy = ContextPolicy.SHARED, fileTypeDetectors = I4GLFileDetector.class)
public class I4GLLanguage extends TruffleLanguage<I4GLContext> {
    public static volatile int counter;

    public static final String ID = "i4gl";
    public static final String MIME_TYPE = "application/x-i4gl";

    // To make the linter happy, remove it
    public static final I4GLLanguage INSTANCE = null;

    private Random random;
    private Map<String, CallTarget> functions;
    private Scanner input = new Scanner(System.in);

    public I4GLLanguage() {
        counter++;
        random = new Random(26270);
        functions = new HashMap<>();
        input = new Scanner(System.in);
    }

    @Override
    protected I4GLContext createContext(Env environment) {
        return new I4GLContext(this, environment);
    }

    @Override
    protected boolean isVisible(I4GLContext context, Object value) {
        return !InteropLibrary.getFactory().getUncached(value).isNull(value);
    }

    @Override
    protected Object findExportedSymbol(I4GLContext state, String globalName, boolean onlyExplicit) {
        return null;
    }

    @Override
    protected Object getLanguageGlobal(I4GLContext i4glState) {
        return i4glState;
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
        I4GLLexer lexer = new I4GLLexer(CharStreams.fromString(source.getCharacters().toString()));
        I4GLParser parser = new I4GLParser(new CommonTokenStream(lexer));
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        BailoutErrorListener listener = new BailoutErrorListener(source);
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);
        I4GLParser.CompilationUnitContext tree = parser.compilationUnit();
        I4GLVisitorImpl visitor = new I4GLVisitorImpl(this, source);
        visitor.visit(tree);
        List<String> errorList = visitor.getErrorList();
        if (errorList.size() > 0) {
            throw new LexicalException(errorList.get(0));
        }
        return Truffle.getRuntime().createCallTarget(visitor.getRootNode());
    }

    /**
     * Resets the random seed.
     */
    public void randomize() {
        random = new Random();
    }

    public int getRandom(int upperBound) {
        return Math.abs(random.nextInt()) % upperBound;
    }

    public void addFunction(String functionIdentifier, I4GLRootNode rootNode) {
        this.functions.put(functionIdentifier, Truffle.getRuntime().createCallTarget(rootNode));
    }

    public CallTarget getFunction(String functionIdentifier) {
        return this.functions.get(functionIdentifier);
    }

    public Scanner getInput() {
        return this.input;
    }

    public void setInput(InputStream is) {
        this.input = new Scanner(is);
    }

    public static I4GLContext getCurrentContext() {
        return getCurrentContext(I4GLLanguage.class);
    }

}
