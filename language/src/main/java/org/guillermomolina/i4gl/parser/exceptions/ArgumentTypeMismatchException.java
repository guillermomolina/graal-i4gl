package org.guillermomolina.i4gl.parser.exceptions;

import com.oracle.truffle.api.source.Source;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception thrown during parsing phase when actual argument type does not
 * match formal argument type.
 */

public class ArgumentTypeMismatchException extends LexicalException {
    private static final long serialVersionUID = 181363083242440832L;

    public ArgumentTypeMismatchException(Source source, ParserRuleContext ctx, int argumentNumber) {
        super(source, ctx, "Argument number " + argumentNumber + " type mismatch");
    }

}
