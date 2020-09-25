package org.guillermomolina.i4gl.parser.exceptions;

import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception thrown during parsing phase when a declaration of an identifier such that was declared previously in the
 * same scope occurs.
 */
public class DuplicitIdentifierException extends LexicalException {

    private static final long serialVersionUID = 7257207106359576073L;

    public DuplicitIdentifierException(Source source, ParserRuleContext ctx, String identifier) {
        super(source, ctx, "Duplicit identifier: " + identifier);
    }
}