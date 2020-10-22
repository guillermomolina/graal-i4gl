package org.guillermomolina.i4gl.parser;

import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.parser.exceptions.BailoutErrorListener;

public class I4GLFullParser {
    private final I4GLNodeFactory factory;

    public I4GLFullParser(final I4GLLanguage language, final Source source) {
        I4GLLexer lexer = new I4GLLexer(CharStreams.fromString(source.getCharacters().toString()));
        I4GLParser parser = new I4GLParser(new CommonTokenStream(lexer));
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        BailoutErrorListener listener = new BailoutErrorListener(source);
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);
        I4GLParser.CompilationUnitContext tree = parser.compilationUnit();
        factory = new I4GLNodeFactory(language, source);
        factory.visit(tree);
    }

    public Map<String, RootCallTarget> getAllFunctions() {
        return factory.getAllFunctions();
    }

    public FrameDescriptor getRootFrameDescriptor() {
        return factory.getRootFrameDescriptor();
    }
}
