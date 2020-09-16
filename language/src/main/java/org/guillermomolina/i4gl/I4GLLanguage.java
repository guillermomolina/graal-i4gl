package org.guillermomolina.i4gl;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLSubroutine;
import org.guillermomolina.i4gl.parser.I4GLParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

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
    private Map<String, VirtualFrame> unitFrames;
    private Map<String, Map<String, I4GLSubroutine>> unitSubroutines;
    private Map<String, I4GLSubroutine> subroutines;
    private Scanner input = new Scanner(System.in);

    public I4GLLanguage() {
        random = new Random(26270);
        unitFrames = new HashMap<>();
        unitSubroutines = new HashMap<>();
        subroutines = new HashMap<>();
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
    protected Object getLanguageGlobal(I4GLState pascalState) {
        return pascalState;
    }

    @Override
    protected boolean isObjectOfLanguage(Object obj) {
        return obj instanceof I4GLSubroutine;
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

    public boolean isUnitRegistered(String unitIdentifier) {
        return this.unitFrames.containsKey(unitIdentifier);
    }

    public VirtualFrame getUnitFrame(String unitIdentifier) {
        return this.unitFrames.get(unitIdentifier);
    }

    public VirtualFrame createUnitFrame(String unitIdentifier, FrameDescriptor frameDescriptor) {
        VirtualFrame unitFrame = Truffle.getRuntime().createVirtualFrame(new Object[0], frameDescriptor);
        this.unitFrames.put(unitIdentifier, unitFrame);

        return unitFrame;
    }

    public void updateSubroutine(String unitIdentifier, String subroutineIdentifier, I4GLRootNode rootNode) {
        if (!this.unitSubroutines.containsKey(unitIdentifier)) {
            this.unitSubroutines.put(unitIdentifier, new HashMap<>());
        }
        this.unitSubroutines.get(unitIdentifier).put(subroutineIdentifier, new I4GLSubroutine(Truffle.getRuntime().createCallTarget(rootNode)));
    }

    public I4GLSubroutine getSubroutine(String unitIdentifier, String subroutineIdentifier) {
        return this.unitSubroutines.get(unitIdentifier).get(subroutineIdentifier);
    }

    public void updateSubroutine(String subroutineIdentifier, I4GLRootNode rootNode) {
        this.subroutines.put(subroutineIdentifier, new I4GLSubroutine(Truffle.getRuntime().createCallTarget(rootNode)));
    }

    public I4GLSubroutine getSubroutine(String subroutineIdentifier) {
        return this.subroutines.get(subroutineIdentifier);
    }

    public Scanner getInput() {
        return this.input;
    }

    public void setInput(InputStream is) {
        this.input = new Scanner(is);
    }

}
