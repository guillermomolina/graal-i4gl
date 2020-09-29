package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.RecordDescriptor;

/**
 * This class represents currently parsed lexical scope. It is a slight wrapper
 * of {@link IdentifiersTable} with some extended functionality. Lexical scope
 * of i4gl are scopes of functions.
 */
public class LexicalScope {

    private String name;
    private final LexicalScope outer;
    private int loopDepth;
    IdentifiersTable localIdentifiers;
    final List<String> arguments;

    /**
     * Default constructor.
     * 
     * @param outer            instance of outer lexical scope
     * @param name             name of the current lexical scope
     * @param usingTPExtension a flag whether support for Turbo I4GL extensions is
     *                         turned on
     */
    LexicalScope(LexicalScope outer, String name) {
        this.name = name;
        this.outer = outer;
        this.localIdentifiers = new IdentifiersTable();
        this.localIdentifiers.addBuiltins();
        this.arguments = new ArrayList<>();
    }

    String getName() {
        return this.name;
    }

    LexicalScope getOuterScope() {
        return this.outer;
    }

    public IdentifiersTable getIdentifiersTable() {
        return this.localIdentifiers;
    }

    public FrameDescriptor getFrameDescriptor() {
        return this.localIdentifiers.getFrameDescriptor();
    }

    FrameSlot getLocalSlot(String identifier) {
        return this.localIdentifiers.getFrameSlot(identifier);
    }

    TypeDescriptor getIdentifierDescriptor(String identifier) {
        return this.localIdentifiers.getIdentifierDescriptor(identifier);
    }

    public void setName(String identifier) {
        this.name = identifier;
    }

    void registerLabel(String identifier) throws LexicalException {
        this.localIdentifiers.addLabel(identifier);
    }

    FrameSlot registerDatabase(String identifier) throws LexicalException {
        return this.localIdentifiers.addDatabase(identifier);
    }

    boolean labelExists(String identifier) {
        return this.localIdentifiers.isLabel(identifier);
    }

    boolean containsLocalIdentifier(String identifier) {
        return this.localIdentifiers.containsIdentifier(identifier);
    }

    boolean containsPublicIdentifier(String identifier) {
        return this.containsLocalIdentifier(identifier);
    }

    void registerLocalVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.localIdentifiers.addVariable(identifier, typeDescriptor);
    }

    void addArgument(String identifier) {
        arguments.add(identifier);
    }

    RecordDescriptor createRecordDescriptor() {
        return new RecordDescriptor(this);
    }

    /**
     * Returns true if the parser is currently inside a loop in the currently parsed
     * source.
     */
    boolean isInLoop() {
        return loopDepth > 0;
    }

    void increaseLoopDepth() {
        ++loopDepth;
    }

    void decreaseLoopDepth() throws LexicalException {
        if (loopDepth == 0) {
            throw new LexicalException("cannot leave cycle");
        } else {
            --loopDepth;
        }
    }

}