package org.guillermomolina.i4gl.parser.types.compound;

import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
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
        return new RecordValue(innerScope.getFrameDescriptor(), innerScope.getIdentifiersTable().getAllIdentifiers());
    }

    public LexicalScope getLexicalScope() {
        return innerScope;
    }

    public boolean containsIdentifier(String identifier) {
        return innerScope.getIdentifiersTable().containsIdentifier(identifier);
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RECORD ");
        FrameDescriptor frameDescriptor = innerScope.getFrameDescriptor();
        Map<String, TypeDescriptor> types = innerScope.getIdentifiersTable().getAllIdentifiers();
        int i = 0;
        for (final FrameSlot slot : frameDescriptor.getSlots()) {
            if (i++!=0) {
                builder.append(", ");
            }
            final String identifier = slot.getIdentifier().toString();
            builder.append(identifier);
            builder.append(" ");
            builder.append(types.get(identifier).toString());
        }
        builder.append(" END RECORD");
        return builder.toString();
    }
}
