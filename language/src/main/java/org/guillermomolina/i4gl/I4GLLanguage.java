package org.guillermomolina.i4gl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.parser.I4GLParser;

/**
 * Representation of our I4GL guest language for Truffle VM. Thanks to the TruffleLanguage.Registration
 * annotation we register this class so that Truffle's PolyglotEngine will use our language.
 */
@TruffleLanguage.Registration(id = I4GLLanguage.ID, name = "I4GLLanguage", defaultMimeType = I4GLLanguage.MIME_TYPE, characterMimeTypes = I4GLLanguage.MIME_TYPE, contextPolicy = ContextPolicy.SHARED, fileTypeDetectors = I4GLFileDetector.class)
public class I4GLLanguage extends TruffleLanguage<I4GLState> {
    public static final String ID = "i4gl";
    public static final String MIME_TYPE = "application/x-i4gl";

    // To make the linter happy, remove it
    public static final I4GLLanguage INSTANCE = null;

    private Random random;
    private Map<String, CallTarget> functions;
    private Scanner input = new Scanner(System.in);

    public I4GLLanguage() {
        random = new Random(26270);
        functions = new HashMap<>();
        input = new Scanner(System.in);
    }

    @Override
    protected I4GLState createContext(Env environment) {
        return new I4GLState();
    }

    @Override
    protected Object findExportedSymbol(I4GLState state, String globalName, boolean onlyExplicit) {
        return null;
    }

    @Override
    protected Object getLanguageGlobal(I4GLState i4glState) {
        return i4glState;
    }

    /**
     * Gets source from the request, parses it and return call target that, if called, executes
     * given script in I4GL language.
     * @param request parsing request
     * @throws I4GLParseException the source cannot be parsed
     */
    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        RootNode mainFunctionNode = I4GLParser.parse(this, source);
        return Truffle.getRuntime().createCallTarget(mainFunctionNode);
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

    public void updateFunction(String functionIdentifier, I4GLRootNode rootNode) {
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

}
