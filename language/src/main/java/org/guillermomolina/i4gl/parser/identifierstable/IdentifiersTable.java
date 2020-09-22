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
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.NilPointerDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.PointerDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.ReferenceDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.NCharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.RecordDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.ConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.RealDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.TextDescriptor;
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
        typeDescriptors.put("CHAR", CharDescriptor.getInstance());
        typeDescriptors.put("TEXT", TextDescriptor.getInstance());

        for (Map.Entry<String, TypeDescriptor> typeEntry : typeDescriptors.entrySet()) {
            identifiersMap.put(typeEntry.getKey(), new TypeTypeDescriptor(typeEntry.getValue()));
        }
    }

    private void addBuiltinConstants() {
        try {
            this.registerNewIdentifier("nil", new NilPointerDescriptor());
        } catch (LexicalException e) {
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

    /**
     * I4GL allows to declare a pointer to a type that is declared after the pointer's declaration. In these cases, we
     * create a pointer type with unspecified inner type but with the identifier of the type to be declared later. After
     * the whole types declaration statement is parsed, this function is called and sets the correct inner type
     * descriptors for each of these pointer types.
     * @throws LexicalException if the inner type was not declared
     */
    public void initializeAllUninitializedPointerDescriptors() throws LexicalException {
        for (Map.Entry<String, TypeDescriptor> typeEntry : this.typeDescriptors.entrySet()) {
            TypeDescriptor type = typeEntry.getValue();
            if (type instanceof PointerDescriptor) {
                PointerDescriptor pointerDescriptor = (PointerDescriptor) type;
                if (!pointerDescriptor.isInnerTypeInitialized()) {
                    TypeDescriptor pointerInnerType = this.getTypeDescriptor(pointerDescriptor.getInnerTypeIdentifier());
                    if (pointerInnerType == null) {
                        throw new LexicalException("Pointer type not declared in the same type statement.");
                    } else {
                        pointerDescriptor.setInnerType(pointerInnerType);
                    }
                }
            }
        }
    }

    public FrameSlot addReference(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        return this.registerNewIdentifier(identifier, new ReferenceDescriptor(typeDescriptor));
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

    public PointerDescriptor createPointerDescriptor(String innerTypeIdentifier) {
        TypeDescriptor innerTypeDescriptor = this.getTypeDescriptor(innerTypeIdentifier);
        return (innerTypeDescriptor == null)? new PointerDescriptor(innerTypeIdentifier) : new PointerDescriptor(innerTypeDescriptor);
    }

    public ArrayDescriptor createArray(int size, TypeDescriptor typeDescriptor) {
        return new ArrayDescriptor(size, typeDescriptor);
    }

    public VarcharDescriptor createVarchar(int size) {
        return new VarcharDescriptor(size);
    }

    public PointerDescriptor createNChar(int size) {
        return new PointerDescriptor(new NCharDescriptor(size));
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
