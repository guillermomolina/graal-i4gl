package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.InitializationNodeFactory;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLSubroutine;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.exceptions.UnknownIdentifierException;
import org.guillermomolina.i4gl.parser.identifierstable.IdentifiersTable;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.FileDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.PointerDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.ArrayDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.EnumTypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.RecordDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.ConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.OrdinalConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.FunctionDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.ReturnTypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.SubroutineDescriptor;
import org.guillermomolina.i4gl.parser.utils.FormalParameter;

/**
 * This class represents currently parsed lexical scope. It is a slight wrapper of {@link IdentifiersTable} with some
 * extended functionality. Lexical scope of i4gl are scopes of subroutines.
 */
public class LexicalScope {

    private String name;
    private final LexicalScope outer;
    private int loopDepth;
    IdentifiersTable localIdentifiers;
    final List<StatementNode> scopeInitializationNodes = new ArrayList<>();

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

    I4GLSubroutine getSubroutine(String identifier) throws UnknownIdentifierException, LexicalException {
        return this.localIdentifiers.getSubroutine(identifier);
    }

    SubroutineDescriptor getSubroutineDescriptor(String identifier, List<ExpressionNode> actualArguments) throws LexicalException {
        SubroutineDescriptor subroutineDescriptor = (SubroutineDescriptor) this.getIdentifierDescriptor(identifier);
        return subroutineDescriptor.getOverload(actualArguments);
    }

    FrameSlot getReturnSlot() {
        return this.localIdentifiers.getFrameSlot(this.name);
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

    void setSubroutineRootNode(I4GLLanguage language, String identifier, I4GLRootNode rootNode) throws UnknownIdentifierException {
        language.updateSubroutine(identifier, rootNode);
        this.localIdentifiers.setSubroutineRootNode(identifier, rootNode);
    }

    void registerLabel(String identifier) throws LexicalException {
        this.localIdentifiers.addLabel(identifier);
    }

    void registerNewType(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.localIdentifiers.addType(identifier, typeDescriptor);
    }

    boolean isParameterlessSubroutine(String identifier) {
        return this.localIdentifiers.isParameterlessSubroutine(identifier);
    }

    boolean isSubroutine(String identifier) {
        return this.localIdentifiers.isSubroutine(identifier);
    }

    boolean labelExists(String identifier) {
        return this.localIdentifiers.isLabel(identifier);
    }

    boolean containsLocalIdentifier(String identifier) {
        return this.localIdentifiers.containsIdentifier(identifier) && !(this.localIdentifiers.getIdentifierDescriptor(identifier) instanceof ReturnTypeDescriptor);
    }

    boolean containsPublicIdentifier(String identifier) {
        return this.containsLocalIdentifier(identifier);
    }

    /**
     * Checks whether current lexical scope contains return variable with the specified identifier.
     */
    boolean containsReturnType(String identifier, boolean onlyPublic) {
        return this.localIdentifiers.containsIdentifier(identifier) &&
                (this.localIdentifiers.getIdentifierDescriptor(identifier) instanceof ReturnTypeDescriptor) &&
                (!onlyPublic || this.containsPublicIdentifier(identifier));
    }

    FrameSlot registerReferenceVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        return this.localIdentifiers.addReference(identifier, typeDescriptor);
    }

    void registerLocalVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.localIdentifiers.addVariable(identifier, typeDescriptor);
    }

    /**
     * Adds initialization scope for this lexical scope. These nodes are prepended to the main block's tree of the
     * subroutine this scope represents. They are required to initialize values of each local variable of the scope.
     * @param initializationNode the new initialization node
     */
    void addScopeInitializationNode(StatementNode initializationNode) {
        this.scopeInitializationNodes.add(initializationNode);
    }

    void registerReturnVariable(TypeDescriptor typeDescriptor) throws LexicalException {
        this.localIdentifiers.addReturnVariable(this.getName(), typeDescriptor);
    }

    ArrayDescriptor createArrayType(OrdinalDescriptor dimension, TypeDescriptor typeDescriptor) {
        return this.localIdentifiers.createArray(dimension, typeDescriptor);
    }

    EnumTypeDescriptor createEnumType(List<String> identifiers) throws LexicalException {
        return this.localIdentifiers.createEnum(identifiers);
    }

    FileDescriptor createFileDescriptor(TypeDescriptor contentTypeDescriptor) {
        return this.localIdentifiers.createFileDescriptor(contentTypeDescriptor);
    }

    RecordDescriptor createRecordDescriptor() {
        return this.localIdentifiers.createRecordDescriptor(this);
    }

    PointerDescriptor createPointerDescriptor(String innerTypeIdentifier) {
        return this.localIdentifiers.createPointerDescriptor(innerTypeIdentifier);
    }

    TypeDescriptor createSetType(OrdinalDescriptor baseType) {
        return this.localIdentifiers.createSetType(baseType);
    }

    /**
     * I4GL allows to declare a pointer to a type that is declared after the pointer's declaration. In these cases, we
     * create a pointer type with unspecified inner type but with the identifier of the type to be declared later. After
     * the whole types declaration statement is parsed, this function is called and sets the correct inner type
     * descriptors for each of these pointer types.
     * @throws LexicalException if the inner type was not declared
     */
    void initializeAllUninitializedPointerDescriptors() throws LexicalException {
        this.localIdentifiers.initializeAllUninitializedPointerDescriptors();
    }

    void registerFunctionInterfaceIfNotForwarded(String identifier, List<FormalParameter> formalParameters, TypeDescriptor returnTypeDescriptor) throws LexicalException {
        this.localIdentifiers.addFunctionInterfaceIfNotForwarded(identifier, formalParameters, returnTypeDescriptor);
    }

    void forwardFunction(String identifier, List<FormalParameter> formalParameters, TypeDescriptor returnTypeDescriptor) throws LexicalException {
        this.localIdentifiers.forwardFunction(identifier, formalParameters, returnTypeDescriptor);
    }

    public void registerBuiltinSubroutine(I4GLLanguage language, String identifier, SubroutineDescriptor descriptor) {
        try {
            this.localIdentifiers.addSubroutine(identifier, descriptor);
            language.updateSubroutine(identifier, descriptor.getRootNode());
        } catch (LexicalException e) {
            throw new I4GLRuntimeException("Could not register builtin subroutine: " + identifier);
        }
    }

    public void registerType(String identifier, TypeDescriptor type) throws LexicalException{
        this.localIdentifiers.addType(identifier, type);
    }

    void registerConstant(String identifier, ConstantDescriptor constant) throws LexicalException {
        this.localIdentifiers.addConstant(identifier, constant);
    }

    FunctionDescriptor registerFunction(String identifier) throws LexicalException {
        return this.localIdentifiers.addFunction(identifier);
    }

    OrdinalDescriptor createRangeDescriptor(OrdinalConstantDescriptor lowerBound, OrdinalConstantDescriptor upperBound)  throws LexicalException {
        if (!lowerBound.getClass().equals(upperBound.getClass())) {
            throw new LexicalException("Range type mismatch");
        }

        long lower = lowerBound.getOrdinalValue();
        long upper = upperBound.getOrdinalValue();

        if (lower > upper) {
            throw new LexicalException("Lower upper bound than lower bound");
        }

        return new OrdinalDescriptor.RangeDescriptor(lowerBound, upperBound);
    }

    OrdinalDescriptor createImplicitRangeDescriptor() {
        return new OrdinalDescriptor.RangeDescriptor(new LongConstantDescriptor(0), new LongConstantDescriptor(1));
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