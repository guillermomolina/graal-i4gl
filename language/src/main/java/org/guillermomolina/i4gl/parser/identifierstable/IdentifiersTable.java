package org.guillermomolina.i4gl.parser.identifierstable;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.LexicalScope;
import org.guillermomolina.i4gl.parser.exceptions.DuplicitIdentifierException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.exceptions.UnknownIdentifierException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeTypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.FileDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.LabelDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.NCharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.RecordDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.TextDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.ConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.RealDescriptor;
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

    public void addType(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.registerNewIdentifier(identifier, new TypeTypeDescriptor(typeDescriptor));
        this.typeDescriptors.put(identifier, typeDescriptor);
    }

    public void addVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.registerNewIdentifier(identifier, typeDescriptor);
    }

    public void addConstant(String identifier, ConstantDescriptor descriptor) throws LexicalException {
        this.registerNewIdentifier(identifier, descriptor);
    }

    public FileDescriptor createFileDescriptor(TypeDescriptor contentTypeDescriptor) {
        return new FileDescriptor(contentTypeDescriptor);
    }

    public RecordDescriptor createRecordDescriptor(LexicalScope recordScope) {
        return new RecordDescriptor(recordScope);
    }

    public ArrayDescriptor createArray(int size, TypeDescriptor typeDescriptor) {
        return new ArrayDescriptor(size, typeDescriptor);
    }

    public NCharDescriptor createNChar(int size) {
        return new NCharDescriptor(size);
    }

    public VarcharDescriptor createVarchar(int size) {
        return new VarcharDescriptor(size);
    }

    public ConstantDescriptor getConstant(String identifier) throws UnknownIdentifierException, LexicalException {
        TypeDescriptor descriptor = this.identifiersMap.get(identifier);
        if (descriptor == null) {
            throw new UnknownIdentifierException(identifier);
        } else if (! (descriptor instanceof ConstantDescriptor)) {
            throw new LexicalException("Not a constant: " + identifier);
        } else {
            return (ConstantDescriptor)descriptor;
        }
    }

    FrameSlot registerNewIdentifier(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        if (this.identifiersMap.containsKey(identifier)){
            throw new DuplicitIdentifierException(identifier);
        } else {
            this.identifiersMap.put(identifier, typeDescriptor);
            return this.frameDescriptor.addFrameSlot(identifier, typeDescriptor.getSlotKind());
        }
    }

}
