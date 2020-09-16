package org.guillermomolina.i4gl.parser.identifierstable.types.subroutine;

import org.guillermomolina.i4gl.parser.utils.FormalParameter;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

import java.util.List;

/**
 * Type descriptor specialized for functions.
 */
public class FunctionDescriptor extends SubroutineDescriptor {

    private TypeDescriptor returnTypeDescriptor;

    public FunctionDescriptor() {
        this.returnTypeDescriptor = null;
    }

    /**
     * The default descriptor.
     * @param formalParameters list of the procedure's formal parameters.
     * @param returnTypeDescriptor return type of the function
     */
    public FunctionDescriptor(List<FormalParameter> formalParameters, TypeDescriptor returnTypeDescriptor) {
        super();
        setFormalParameters(formalParameters);
        this.returnTypeDescriptor = returnTypeDescriptor;
    }

    public TypeDescriptor getReturnDescriptor() {
        return this.returnTypeDescriptor;
    }

    public void setReturnDescriptor(TypeDescriptor returnTypeDescriptor) {
        this.returnTypeDescriptor = returnTypeDescriptor;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return (type instanceof FunctionDescriptor) && super.convertibleTo(type);
    }

}
