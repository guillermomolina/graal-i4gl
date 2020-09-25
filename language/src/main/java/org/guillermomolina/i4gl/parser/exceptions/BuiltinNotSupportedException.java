package org.guillermomolina.i4gl.parser.exceptions;

import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception thrown during parsing phase when user uses a built-in function that is not supported in Trupple.
 */
public class BuiltinNotSupportedException extends LexicalException {

    private static final long serialVersionUID = 2785716297448745948L;

    public BuiltinNotSupportedException(Source source, ParserRuleContext ctx) {
        super(source, ctx, "This builtin is not supported");
    }

}
