package org.guillermomolina.i4gl.parser;

/**
 * Specified lexical scope for records. Not really useful in its current state.
 */
class RecordLexicalScope extends I4GLLexicalScope {

    RecordLexicalScope(I4GLLexicalScope outer) {
        super(outer, "_record");
    }

}
