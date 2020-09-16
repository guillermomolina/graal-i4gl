package org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.builtin;

import com.oracle.truffle.api.frame.FrameDescriptor;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.statement.StatementNode;
import org.guillermomolina.i4gl.nodes.root.ProcedureI4GLRootNode;
import org.guillermomolina.i4gl.parser.utils.FormalParameter;
import org.guillermomolina.i4gl.parser.exceptions.BuiltinNotSupportedException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.ProcedureDescriptor;

import java.util.Collections;
import java.util.List;

/**
 * Base type descriptor for type descriptors for I4GL's built-in procedures. Additionally to {@link ProcedureDescriptor}
 * it sets the procedure's's root node in its constructor.
 */
public abstract class BuiltinProcedureDescriptor extends ProcedureDescriptor {

    /**
     * The default constructor.
     * @param bodyNode body node of the function
     * @param parameters list of formal parameters of the function
     */
    BuiltinProcedureDescriptor(StatementNode bodyNode, List<FormalParameter> parameters) {
        super(parameters);
        this.setRootNode(new ProcedureI4GLRootNode(I4GLLanguage.INSTANCE, new FrameDescriptor(), bodyNode));
    }

    @Override
    public boolean isSubroutineParameter(int index) {
        return false;
    }


    /**
     * Specialized type descriptor for I4GL's built-in procedures whose parameters are not passed by reference.
     */
    public static class NoReferenceParameterBuiltin extends BuiltinProcedureDescriptor {

        /**
         * The default constructor.
         * @param bodyNode body node of the function
         * @param parameters list of formal parameters of the function
         */
        public NoReferenceParameterBuiltin(StatementNode bodyNode, List<FormalParameter> parameters) {
            super(bodyNode, parameters);
        }

        @Override
        public boolean isReferenceParameter(int parameterIndex) {
            return false;
        }

    }

    /**
     * Specialized type descriptor for I4GL's built-in procedures whose parameters are all passed by reference.
     */
    public static class FullReferenceParameterBuiltin extends BuiltinProcedureDescriptor {

        /**
         * The default constructor.
         * @param bodyNode body node of the function
         * @param parameters list of formal parameters of the function
         */
        public FullReferenceParameterBuiltin(StatementNode bodyNode, List<FormalParameter> parameters) {
            super(bodyNode, parameters);
        }

        @Override
        public boolean isReferenceParameter(int index) {
            return true;
        }

    }

    /**
     * Specialized built-in procedure type descriptor for procedures with single argument.
     */
    public static class OneArgumentBuiltin extends BuiltinProcedureDescriptor {

        /**
         * The default constructor.
         * @param bodyNode the body node of the procedure
         * @param parameter the single procedure's formal parameter
         */
        public OneArgumentBuiltin(StatementNode bodyNode, FormalParameter parameter) {
            super(bodyNode, Collections.singletonList(parameter));
        }

        @Override
        public boolean hasParameters() {
            return true;
        }

    }

    /**
     * Specialized built-in procedure type descriptor for unsupported built-in procedures.
     */
    public static class NotSupportedSubroutine extends NoReferenceParameterBuiltin {

        public NotSupportedSubroutine() {
            super(null, Collections.emptyList());
        }

        @Override
        public void verifyArguments(List<ExpressionNode> passedArguments) throws LexicalException {
            throw new BuiltinNotSupportedException();
        }

    }

    /**
     * Specialized built-in procedure type descriptor for procedures with zero arguments.
     */
    public static class NoArgumentBuiltin extends BuiltinProcedureDescriptor {

        /**
         * The default constructor.
         * @param bodyNode the body node of the procedure
         */
        public NoArgumentBuiltin(StatementNode bodyNode) {
            super(bodyNode, Collections.emptyList());
        }

    }

}
