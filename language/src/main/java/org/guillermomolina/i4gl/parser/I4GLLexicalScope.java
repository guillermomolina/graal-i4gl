package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.exceptions.DuplicitIdentifierException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.complex.DatabaseDescriptor;
import org.guillermomolina.i4gl.parser.types.complex.LabelDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.RecordDescriptor;

/**
 * This class represents currently parsed lexical scope. Lexical scope
 * of i4gl are scopes of functions.
 */
public class I4GLLexicalScope {

    /**
     * Map of all identifiers: e.g.: variable names, function names, types names,
     * ...
     */
    private String name;
    private Map<String, I4GLTypeDescriptor> identifiersMap;
    final List<String> arguments;
    private FrameDescriptor frameDescriptor;
    private final I4GLLexicalScope outer;
    private int loopDepth;

    /**
     * Default constructor.
     * 
     * @param outer            instance of outer lexical scope
     * @param name             name of the current lexical scope
     * @param usingTPExtension a flag whether support for Turbo I4GL extensions is
     *                         turned on
     */
    I4GLLexicalScope(I4GLLexicalScope outer, String name) {
        this.identifiersMap = new HashMap<>();
        this.frameDescriptor = new FrameDescriptor();
        this.name = name;
        this.outer = outer;
        this.arguments = new ArrayList<>();
    }
   

    public FrameSlot getFrameSlot(String identifier) {
        return frameDescriptor.findFrameSlot(identifier);
    }

    public FrameDescriptor getFrameDescriptor() {
        return frameDescriptor;
    }

    public I4GLTypeDescriptor getIdentifierDescriptor(String identifier) {
        return identifiersMap.get(identifier);
    }

    public Map<String, I4GLTypeDescriptor> getAllIdentifiers() {
        return identifiersMap;
    }

    public boolean containsIdentifier(String identifier) {
        return identifiersMap.containsKey(identifier);
    }

    public boolean isLabel(String identifier) {
        return identifiersMap.get(identifier) instanceof LabelDescriptor;
    }

    public void addLabel(String identifier) throws LexicalException {
        registerNewIdentifier(identifier, new LabelDescriptor(identifier));
    }

    public FrameSlot registerDatabase(String identifier) throws LexicalException {
        return registerNewIdentifier("_database", new DatabaseDescriptor(identifier));
    }

    public void addVariable(String identifier, I4GLTypeDescriptor typeDescriptor) throws LexicalException {
        registerNewIdentifier(identifier, typeDescriptor);
    }

    FrameSlot getLocalSlot(String identifier) {
        return getFrameSlot(identifier);
    }

    FrameSlot registerNewIdentifier(String identifier, I4GLTypeDescriptor typeDescriptor) throws LexicalException,
            DuplicitIdentifierException {
        if (identifiersMap.containsKey(identifier)){
            throw new DuplicitIdentifierException(identifier);
        } else {
            identifiersMap.put(identifier, typeDescriptor);
            return frameDescriptor.addFrameSlot(identifier, typeDescriptor.getSlotKind());
        }
    }

    String getName() {
        return name;
    }

    I4GLLexicalScope getOuterScope() {
        return outer;
    }

    public void setName(String identifier) {
        name = identifier;
    }

    boolean labelExists(String identifier) {
        return isLabel(identifier);
    }

    boolean containsLocalIdentifier(String identifier) {
        return containsIdentifier(identifier);
    }

    boolean containsPublicIdentifier(String identifier) {
        return containsLocalIdentifier(identifier);
    }

    void registerLocalVariable(String identifier, I4GLTypeDescriptor typeDescriptor) throws LexicalException {
        addVariable(identifier, typeDescriptor);
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