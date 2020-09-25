package org.guillermomolina.i4gl.parser.exceptions;

import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/**
 * A generic exception for lexical errors. They are thrown during parsing phase, caught in the {@link org.guillermomolina.i4gl.parser.NodeFactory}
 * and their message is printed to the error output stream.
 */
public class LexicalException extends ParseException {

    private static final long serialVersionUID = -5362208927595320271L;

    public LexicalException(Source source, ParserRuleContext ctx, String message) {
        super(source, ctx.start.getLine(), ctx.start.getCharPositionInLine() + 1,
                ctx.stop.getStopIndex() - ctx.start.getStartIndex(), message);
    }

    public LexicalException(Source source, Token token, String message) {
        super(source, token.getLine(), token.getCharPositionInLine() + 1, token.getStopIndex() - token.getStartIndex(),
                message);
    }
}