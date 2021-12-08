package i4gl.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import i4gl.parser.exceptions.DuplicitIdentifierException;
import i4gl.parser.exceptions.LexicalException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.complex.DatabaseType;
import i4gl.runtime.types.complex.LabelType;
import i4gl.runtime.types.compound.RecordType;

/**
 * This class represents currently parsed lexical scope. Lexical scope
 * of i4gl are scopes of functions.
 */
public class ParseScope {

    /**
     * Map of all identifiers: e.g.: variable names, function names, types names,
     * ...
     */
    private String name;
    private String type;
    private Map<String, BaseType> variables;
    final List<String> arguments;
    private FrameDescriptor frameDescriptor;
    private final ParseScope outer;
    private int loopDepth;

    public static final String GLOBAL_TYPE = "GLOBAL";
    public static final String MODULE_TYPE = "MODULE";
    public static final String FUNCTION_TYPE = "FUNCTION";
    public static final String RECORD_TYPE = "RECORD";
    public static final String DATABASE_IDENTIFIER = "!database";
    public static final String SQLCA_IDENTIFIER = "sqlca";

    /**
     * Default constructor.
     * 
     * @param outer            instance of outer lexical scope
     * @param name             name of the current lexical scope
     */
    ParseScope(ParseScope outer, String type, String name) {
        this.variables = new LinkedHashMap<>();
        this.frameDescriptor = new FrameDescriptor();
        this.name = name;
        this.outer = outer;
        this.arguments = new ArrayList<>();
        this.type = type;
    }

    public FrameSlot getFrameSlot(String identifier) {
        return frameDescriptor.findFrameSlot(identifier);
    }

    public FrameDescriptor getFrameDescriptor() {
        return frameDescriptor;
    }

    public BaseType getIdentifierType(String identifier) {
        return variables.get(identifier);
    }

    public Map<String, BaseType> getVariables() {
        return variables;
    }

    public boolean containsIdentifier(String identifier) {
        return variables.containsKey(identifier);
    }

    public boolean isLabel(String identifier) {
        return variables.get(identifier) instanceof LabelType;
    }

    public FrameSlot addLabel(String identifier) throws LexicalException {
        return registerNewIdentifier(identifier, new LabelType(identifier));
    }

    public FrameSlot addVariable(String identifier, BaseType type) throws LexicalException {
        return registerNewIdentifier(identifier, type);
    }

    public FrameSlot addDatabaseVariable(final String alias) throws LexicalException {
        return registerNewIdentifier(DATABASE_IDENTIFIER, new DatabaseType(alias));
    }

    public FrameSlot addSqlcaVariable() throws LexicalException {
        return registerNewIdentifier(SQLCA_IDENTIFIER, RecordType.SQLCA);
    }

    FrameSlot getLocalSlot(String identifier) {
        return getFrameSlot(identifier);
    }

    FrameSlot registerNewIdentifier(String identifier, BaseType type) throws LexicalException {
        if (variables.containsKey(identifier)){
            throw new DuplicitIdentifierException(identifier);
        } else {
            variables.put(identifier, type);
            return frameDescriptor.addFrameSlot(identifier, type.getSlotKind());
        }
    }

    String getName() {
        return name == null? type : name;
    }

    @Override
    public String toString() {
        return type + "(" + (name == null? "" : name) + ")";
    }

    ParseScope getOuterScope() {
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

    FrameSlot registerLocalVariable(String identifier, BaseType type) throws LexicalException {
        return addVariable(identifier, type);
    }

    void addArgument(String identifier) {
        arguments.add(identifier);
    }

    RecordType createRecordType() {
        return new RecordType(variables);
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