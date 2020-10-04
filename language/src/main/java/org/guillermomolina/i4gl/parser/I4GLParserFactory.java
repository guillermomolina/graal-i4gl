package org.guillermomolina.i4gl.parser;

import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.parser.exceptions.BailoutErrorListener;

public final class I4GLParserFactory {

    private I4GLParserFactory() {
    }

    public static Map<String, RootCallTarget> parseI4GL(I4GLLanguage language, Source source) {
        I4GLLexer lexer = new I4GLLexer(CharStreams.fromString(source.getCharacters().toString()));
        I4GLParser parser = new I4GLParser(new CommonTokenStream(lexer));
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        BailoutErrorListener listener = new BailoutErrorListener(source);
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);
        I4GLParser.CompilationUnitContext tree = parser.compilationUnit();
        I4GLNodeFactory factory = new I4GLNodeFactory(language, source);
        factory.visit(tree);
        return factory.getAllFunctions();
    }
}
