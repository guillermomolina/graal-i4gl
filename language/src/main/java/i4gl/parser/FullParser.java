package i4gl.parser;

import java.util.Map;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import i4gl.I4GLLanguage;
import i4gl.parser.exceptions.BailoutErrorListener;

public class FullParser {
    private final NodeParserVisitor factory;

    public FullParser(final I4GLLanguage language, final Source source) {
        I4GLLexer lexer = new I4GLLexer(CharStreams.fromString(source.getCharacters().toString()));
        I4GLParser parser = new I4GLParser(new CommonTokenStream(lexer));
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        BailoutErrorListener listener = new BailoutErrorListener(source);
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);
        I4GLParser.ModuleContext tree = parser.module();
        factory = new NodeParserVisitor(language, source);
        factory.visit(tree);
    }

    public String getModuleName() {
        return factory.getModuleName();
    }

    public Map<String, RootCallTarget> getAllFunctions() {
        return factory.getAllFunctions();
    }

    public FrameDescriptor getGlobalsFrameDescriptor() {
        return factory.getGlobalsFrameDescriptor();
    }

    public FrameDescriptor getModuleFrameDescriptor() {
        return factory.getModuleFrameDescriptor();
    }
}
