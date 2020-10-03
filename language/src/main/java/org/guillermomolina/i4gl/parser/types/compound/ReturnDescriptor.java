package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.runtime.exceptions.I4GLRuntimeException;

/**
 * Type descriptor for I4GL's returns types. It contains additional information about the variables it contains.
 */
public class ReturnDescriptor implements I4GLTypeDescriptor {
    private final I4GLTypeDescriptor[] valueDescriptors;

    /**
     * The default descriptor.
     * @param innerScope lexical scope containing the identifiers of the variables this return contains
     */
    public ReturnDescriptor(I4GLTypeDescriptor[] valueDescriptors) {
        this.valueDescriptors = valueDescriptors;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        throw new I4GLRuntimeException("Can not be here");
    }

    public int getSize() {
        return valueDescriptors.length;
    }

    public boolean isVoid() {
        return valueDescriptors.length == 0;
    }

    public I4GLTypeDescriptor getValueDescriptor(final int index) {
        return this.valueDescriptors[index];
    }

    @Override
    public boolean convertibleTo(I4GLTypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < valueDescriptors.length; i++) {
            if (i!=0) {
                builder.append(", ");
            }
            builder.append(valueDescriptors[i].toString());
        }
        return builder.toString();
    }
}
