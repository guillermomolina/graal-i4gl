package org.guillermomolina.i4gl.parser;

/**
 * Specified lexical scope for records. Not really useful in its current state.
 */
class RecordLexicalScope extends I4GLParseScope {

    RecordLexicalScope(I4GLParseScope outer) {
        super(outer, "_record");
    }

}
