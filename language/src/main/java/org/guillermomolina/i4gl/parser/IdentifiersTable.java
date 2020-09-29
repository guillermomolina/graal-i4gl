package org.guillermomolina.i4gl.parser;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.exceptions.DuplicitIdentifierException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.types.TypeTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.complex.DatabaseDescriptor;
import org.guillermomolina.i4gl.parser.types.complex.LabelDescriptor;
import org.guillermomolina.i4gl.parser.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * This is a storage for all identifiers encountered during the parsing phase.
 */
public class IdentifiersTable {

    /**
     * Map of all identifiers: e.g.: variable names, function names, types names,
     * ...
     */
    private Map<String, TypeDescriptor> identifiersMap;

    /** Map of type identifiers: e.g.: integer, boolean, enums, records, ... */
    Map<String, TypeDescriptor> typeDescriptors;

    private FrameDescriptor frameDescriptor;

    public IdentifiersTable() {
        this.initialize();
    }

    public void addBuiltins() {
        addBuiltinTypes();
        addBuiltinConstants();
        addBuiltinFunctions();
    }

    private void initialize() {
        this.identifiersMap = new HashMap<>();
        this.typeDescriptors = new HashMap<>();
        this.frameDescriptor = new FrameDescriptor();
    }

    protected void addBuiltinTypes() {
        typeDescriptors.put("SMALLINT", IntDescriptor.getInstance());
        typeDescriptors.put("INTEGER", IntDescriptor.getInstance());
        typeDescriptors.put("INT", IntDescriptor.getInstance());
        typeDescriptors.put("BIGINT", LongDescriptor.getInstance());
        typeDescriptors.put("FLOAT", RealDescriptor.getInstance());
        typeDescriptors.put("DOUBLE", RealDescriptor.getInstance());
        typeDescriptors.put("INT8", CharDescriptor.getInstance());
        typeDescriptors.put("TEXT", TextDescriptor.getInstance());

        for (Map.Entry<String, TypeDescriptor> typeEntry : typeDescriptors.entrySet()) {
            identifiersMap.put(typeEntry.getKey(), new TypeTypeDescriptor(typeEntry.getValue()));
        }
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

    public TypeDescriptor getIdentifierDescriptor(String identifier) {
        return this.identifiersMap.get(identifier);
    }

    public TypeDescriptor getTypeDescriptor(String identifier)  {
        return this.typeDescriptors.get(identifier);
    }

    public Map<String, TypeDescriptor> getAllIdentifiers() {
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

    public void addType(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.registerNewIdentifier(identifier, new TypeTypeDescriptor(typeDescriptor));
        this.typeDescriptors.put(identifier, typeDescriptor);
    }

    public void addVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.registerNewIdentifier(identifier, typeDescriptor);
    }

    FrameSlot registerNewIdentifier(String identifier, TypeDescriptor typeDescriptor) throws LexicalException,
            DuplicitIdentifierException {
        if (this.identifiersMap.containsKey(identifier)){
            throw new DuplicitIdentifierException(identifier);
        } else {
            this.identifiersMap.put(identifier, typeDescriptor);
            return this.frameDescriptor.addFrameSlot(identifier, typeDescriptor.getSlotKind());
        }
    }

}
