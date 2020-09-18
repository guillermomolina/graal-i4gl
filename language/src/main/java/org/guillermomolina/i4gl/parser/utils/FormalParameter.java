package org.guillermomolina.i4gl.parser.utils;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.function.FunctionDescriptor;

/**
 * Structure representing a function formal parameter. It contains the identifier of the parameter, its type descriptor,
 * flag whether it is a reference passed parameter and descriptor of the function to which it belongs.
 */
public class FormalParameter {
    public FormalParameter(String identifier, TypeDescriptor type, FunctionDescriptor function) {
        this.type = type;
        this.identifier = identifier;

        if (function != null) {
            this.isFunction = true;
            this.function = function;
        }
    }

    public FormalParameter(String identifier, TypeDescriptor type) {
        this(identifier, type, null);
    }

    public FormalParameter(String identifier) {
        this(identifier, null, null);
    }

    public TypeDescriptor type;
    public String identifier;
    public boolean isFunction;
    public FunctionDescriptor function;
}