package org.guillermomolina.i4gl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;

import org.guillermomolina.i4gl.parser.exceptions.DuplicitIdentifierException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.complex.I4GLDatabaseType;
import org.guillermomolina.i4gl.runtime.types.complex.I4GLLabelType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLRecordType;

/**
 * This class represents currently parsed lexical scope. Lexical scope
 * of i4gl are scopes of functions.
 */
public class I4GLParseScope {

    /**
     * Map of all identifiers: e.g.: variable names, function names, types names,
     * ...
     */
    private String name;
    private String type;
    private Map<String, I4GLType> variables;
    final List<String> arguments;
    private FrameDescriptor frameDescriptor;
    private final I4GLParseScope outer;
    private int loopDepth;

    public static final String GLOBAL_TYPE = "GLOBAL";
    public static final String MODULE_TYPE = "MODULE";
    public static final String FUNCTION_TYPE = "FUNCTION";
    public static final String RECORD_TYPE = "RECORD";
    public static final String DATABASE_IDENTIFIER = "!database";

    /**
     * Default constructor.
     * 
     * @param outer            instance of outer lexical scope
     * @param name             name of the current lexical scope
     */
    I4GLParseScope(I4GLParseScope outer, String type, String name) {
        this.variables = new HashMap<>();
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

    public I4GLType getIdentifierType(String identifier) {
        return variables.get(identifier);
    }

    public Map<String, I4GLType> getVariables() {
        return variables;
    }

    public boolean containsIdentifier(String identifier) {
        return variables.containsKey(identifier);
    }

    public boolean isLabel(String identifier) {
        return variables.get(identifier) instanceof I4GLLabelType;
    }

    public FrameSlot addLabel(String identifier) throws LexicalException {
        return registerNewIdentifier(identifier, new I4GLLabelType(identifier));
    }

    public FrameSlot addVariable(String identifier, I4GLType type) throws LexicalException {
        return registerNewIdentifier(identifier, type);
    }

    public FrameSlot addDatabaseVariable() throws LexicalException {
        return registerNewIdentifier(DATABASE_IDENTIFIER, I4GLDatabaseType.SINGLETON);
    }

    FrameSlot getLocalSlot(String identifier) {
        return getFrameSlot(identifier);
    }

    FrameSlot registerNewIdentifier(String identifier, I4GLType type) throws LexicalException {
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

    I4GLParseScope getOuterScope() {
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

    FrameSlot registerLocalVariable(String identifier, I4GLType type) throws LexicalException {
        return addVariable(identifier, type);
    }

    void addArgument(String identifier) {
        arguments.add(identifier);
    }

    I4GLRecordType createRecordType() {
        return new I4GLRecordType(variables);
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