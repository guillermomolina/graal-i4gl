package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.LexicalScope;
import org.guillermomolina.i4gl.parser.types.TypeDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.RecordValue;

/**
 * Type descriptor for I4GL's records types. It contains additional information about the variables it contains.
 */
public class RecordDescriptor implements TypeDescriptor {

    private final LexicalScope innerScope;

    /**
     * The default descriptor.
     * @param innerScope lexical scope containing the identifiers of the variables this record contains
     */
    public RecordDescriptor(LexicalScope innerScope) {
        this.innerScope = innerScope;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new RecordValue(this.innerScope.getFrameDescriptor(), innerScope.getIdentifiersTable().getAllIdentifiers());
    }

    public LexicalScope getLexicalScope() {
        return this.innerScope;
    }

    public boolean containsIdentifier(String identifier) {
        return this.innerScope.getIdentifiersTable().containsIdentifier(identifier);
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return false;
    }

}
