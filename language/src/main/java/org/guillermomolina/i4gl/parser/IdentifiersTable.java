package org.guillermomolina.i4gl.parser;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.exceptions.DuplicitIdentifierException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.complex.DatabaseDescriptor;
import org.guillermomolina.i4gl.parser.types.complex.LabelDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * This is a storage for all identifiers encountered during the parsing phase.
 */
public class IdentifiersTable {

    /**
     * Map of all identifiers: e.g.: variable names, function names, types names,
     * ...
     */
    private Map<String, I4GLTypeDescriptor> identifiersMap;

    private FrameDescriptor frameDescriptor;

    public IdentifiersTable() {
        this.initialize();
    }

    public void addBuiltins() {
        addBuiltinConstants();
        addBuiltinFunctions();
    }

    private void initialize() {
        this.identifiersMap = new HashMap<>();
        this.frameDescriptor = new FrameDescriptor();
    }

    private void addBuiltinConstants() {
        try {
            // TODO
        } catch (Exception e) {
            throw new I4GLRuntimeException("Could not initialize builtin constants");
        }
    }

    protected void addBuiltinFunctions() {
        // TODO
    }

    public FrameSlot getFrameSlot(String identifier) {
        return this.frameDescriptor.findFrameSlot(identifier);
    }

    public FrameDescriptor getFrameDescriptor() {
        return this.frameDescriptor;
    }

    public I4GLTypeDescriptor getIdentifierDescriptor(String identifier) {
        return this.identifiersMap.get(identifier);
    }

    public Map<String, I4GLTypeDescriptor> getAllIdentifiers() {
        return this.identifiersMap;
    }

    public boolean containsIdentifier(String identifier) {
        return this.identifiersMap.containsKey(identifier);
    }

    public boolean isLabel(String identifier) {
        return this.identifiersMap.get(identifier) instanceof LabelDescriptor;
    }

    public void addLabel(String identifier) throws LexicalException {
        this.registerNewIdentifier(identifier, new LabelDescriptor(identifier));
    }

    public FrameSlot addDatabase(String identifier) throws LexicalException {
        return this.registerNewIdentifier("_database", new DatabaseDescriptor(identifier));
    }

    public void addVariable(String identifier, I4GLTypeDescriptor typeDescriptor) throws LexicalException {
        this.registerNewIdentifier(identifier, typeDescriptor);
    }

    FrameSlot registerNewIdentifier(String identifier, I4GLTypeDescriptor typeDescriptor) throws LexicalException,
            DuplicitIdentifierException {
        if (this.identifiersMap.containsKey(identifier)){
            throw new DuplicitIdentifierException(identifier);
        } else {
            this.identifiersMap.put(identifier, typeDescriptor);
            return this.frameDescriptor.addFrameSlot(identifier, typeDescriptor.getSlotKind());
        }
    }

}
