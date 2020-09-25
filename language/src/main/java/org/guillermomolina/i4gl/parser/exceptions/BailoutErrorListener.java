package org.guillermomolina.i4gl.parser.exceptions;

import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class BailoutErrorListener extends BaseErrorListener {
    private final Source source;
    
    public BailoutErrorListener(Source source) {
        this.source = source;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        Token token = (Token) offendingSymbol;
        throw new LexicalException(source, token, msg);
    }
}
