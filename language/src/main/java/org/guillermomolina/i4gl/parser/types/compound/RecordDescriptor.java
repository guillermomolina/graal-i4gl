package org.guillermomolina.i4gl.parser.types.compound;

import java.util.Map;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.I4GLParseScope;
import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.RecordValue;

/**
 * Type descriptor for I4GL's records types. It contains additional information about the variables it contains.
 */
public class RecordDescriptor implements I4GLTypeDescriptor {

    private final I4GLParseScope innerScope;

    /**
     * The default descriptor.
     * @param innerScope lexical scope containing the identifiers of the variables this record contains
     */
    public RecordDescriptor(I4GLParseScope innerScope) {
        this.innerScope = innerScope;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return new RecordValue(innerScope.getFrameDescriptor(), innerScope.getAllIdentifiers());
    }

    public I4GLParseScope getLexicalScope() {
        return innerScope;
    }

    public boolean containsIdentifier(String identifier) {
        return innerScope.containsIdentifier(identifier);
    }

    @Override
    public boolean convertibleTo(I4GLTypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RECORD ");
        FrameDescriptor frameDescriptor = innerScope.getFrameDescriptor();
        Map<String, I4GLTypeDescriptor> types = innerScope.getAllIdentifiers();
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
