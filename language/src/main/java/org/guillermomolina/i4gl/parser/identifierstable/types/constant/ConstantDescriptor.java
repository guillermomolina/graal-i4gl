package org.guillermomolina.i4gl.parser.identifierstable.types.constant;

import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

/**
 * Interface for type descriptors for constants.
 */
public interface ConstantDescriptor extends TypeDescriptor {

    /**
     * Gets the value of the constant/
     */
    Object getValue();

    /**
     * Checks whether this constant is signed (e.g.: integer vs. string).
     */
    boolean isSigned();

    /**
     * Gets the negated copy of this constant
     */
    ConstantDescriptor negatedCopy() throws LexicalException;

    /**
     * Gets the type of the constant.
     */
    TypeDescriptor getType();

}
