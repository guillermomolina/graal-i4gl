package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.exceptions.UnknownIdentifierException;
import org.guillermomolina.i4gl.parser.identifierstable.IdentifiersTable;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.FileDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.NCharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.RecordDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.VarcharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.ConstantDescriptor;

/**
 * This class represents currently parsed lexical scope. It is a slight wrapper of {@link IdentifiersTable} with some
 * extended functionality. Lexical scope of i4gl are scopes of functions.
 */
public class LexicalScope {

    private String name;
    private final LexicalScope outer;
    private int loopDepth;
    IdentifiersTable localIdentifiers; 
    final List<String> arguments;

    /**
     * Default constructor.
     * @param outer instance of outer lexical scope
     * @param name name of the current lexical scope
     * @param usingTPExtension a flag whether support for Turbo I4GL extensions is turned on
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

    TypeDescriptor getTypeDescriptor(String identifier) {
        return this.localIdentifiers.getTypeDescriptor(identifier);
    }

    ConstantDescriptor getConstant(String identifier) throws UnknownIdentifierException, LexicalException {
        return this.localIdentifiers.getConstant(identifier);
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

    void registerNewType(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.localIdentifiers.addType(identifier, typeDescriptor);
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

    ArrayDescriptor createArrayType(int size, TypeDescriptor typeDescriptor) {
        return this.localIdentifiers.createArray(size, typeDescriptor);
    }

    NCharDescriptor createNCharType(int size) {
        return this.localIdentifiers.createNChar(size);
    }

    VarcharDescriptor createVarcharType(int size) {
        return this.localIdentifiers.createVarchar(size);
    }

    FileDescriptor createFileDescriptor(TypeDescriptor contentTypeDescriptor) {
        return this.localIdentifiers.createFileDescriptor(contentTypeDescriptor);
    }

    RecordDescriptor createRecordDescriptor() {
        return this.localIdentifiers.createRecordDescriptor(this);
    }

    public void registerType(String identifier, TypeDescriptor type) throws LexicalException{
        this.localIdentifiers.addType(identifier, type);
    }

    void registerConstant(String identifier, ConstantDescriptor constant) throws LexicalException {
        this.localIdentifiers.addConstant(identifier, constant);
    }

    /**
     * Returns true if the parser is currently inside a loop in the currently parsed source.
     */
    boolean isInLoop() {
        return loopDepth > 0;
    }

    void increaseLoopDepth() {
        ++loopDepth;
    }

    void decreaseLoopDepth() throws LexicalException {
        if (loopDepth == 0) {
            throw new LexicalException("Cannot leave cycle.");
        } else {
            --loopDepth;
        }
    }

}