package org.guillermomolina.i4gl.parser.identifierstable.types.subroutine;

import org.guillermomolina.i4gl.parser.utils.FormalParameter;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;

import java.util.List;

/**
 * Type descriptor specialized for procedures.
 */
public class ProcedureDescriptor extends SubroutineDescriptor {

    /**
     * The default descriptor.
     * @param formalParameters list of the procedure's formal parameters.
     */
    public ProcedureDescriptor(List<FormalParameter> formalParameters) {
        super();
        setFormalParameters(formalParameters);
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return (type instanceof ProcedureDescriptor) && super.convertibleTo(type);
    }

}
