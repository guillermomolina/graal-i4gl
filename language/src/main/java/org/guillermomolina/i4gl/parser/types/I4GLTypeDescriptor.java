package org.guillermomolina.i4gl.parser.types;

import com.oracle.truffle.api.frame.FrameSlotKind;

/**
 * Interface for all type descriptors.
 */
public interface I4GLTypeDescriptor {

    /**
     * Gets the {@link FrameSlotKind} of the value that is represented by this descriptor.
     */
    FrameSlotKind getSlotKind();

    /**
     * Gets the default value of this type. It is used mainly for initialization of variables.
     */
    Object getDefaultValue();

    /**
     * Checks whether this type is convertible to the selected type.
     */
    boolean convertibleTo(I4GLTypeDescriptor typeDescriptor);

}

