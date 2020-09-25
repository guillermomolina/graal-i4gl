package org.guillermomolina.i4gl.parser.exceptions;

import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception thrown during parsing phase when a method call with wrong number of arguments occurs.
 */
public class IncorrectNumberOfArgumentsProvidedException extends LexicalException {

    /**
     *
     */
    private static final long serialVersionUID = -6531664182149975980L;

    public IncorrectNumberOfArgumentsProvidedException(Source source, ParserRuleContext ctx, int required, int given) {
        super(source, ctx, "Incorrect number of parameters provided. Required " + required + ", given: " + given);
    }

}
