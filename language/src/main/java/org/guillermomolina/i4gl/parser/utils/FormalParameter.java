package org.guillermomolina.i4gl.parser.utils;

import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.SubroutineDescriptor;

/**
 * Structure representing a subroutine formal parameter. It contains the identifier of the parameter, its type descriptor,
 * flag whether it is a reference passed parameter and descriptor of the subroutine to which it belongs.
 */
public class FormalParameter {
    public FormalParameter(String identifier, TypeDescriptor type, SubroutineDescriptor subroutine) {
        this.type = type;
        this.identifier = identifier;

        if (subroutine != null) {
            this.isSubroutine = true;
            this.subroutine = subroutine;
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
    public boolean isSubroutine;
    public SubroutineDescriptor subroutine;
}