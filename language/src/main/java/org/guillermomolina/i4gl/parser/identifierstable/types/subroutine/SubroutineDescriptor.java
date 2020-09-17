package org.guillermomolina.i4gl.parser.identifierstable.types.subroutine;

import java.util.List;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.nodes.RootNode;

import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.parser.exceptions.ArgumentTypeMismatchException;
import org.guillermomolina.i4gl.parser.exceptions.IncorrectNumberOfArgumentsProvidedException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.utils.FormalParameter;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLSubroutine;

/**
 * Base type descriptor for subroutines. It contains the list of its formal parameters and an instance of {@link I4GLSubroutine}
 * representing the subroutine.
 */
public abstract class SubroutineDescriptor implements TypeDescriptor {

    protected List<FormalParameter> formalParameters;

    protected I4GLSubroutine subroutine;

    /**
     * The default constructor.
     * @param formalParameters list of the subroutine's formal parameters
     */
    SubroutineDescriptor() {
        this.formalParameters = null;
    }

    /**
     * The default constructor.
     * @param formalParameters list of the subroutine's formal parameters
     */
    /*SubroutineDescriptor(List<FormalParameter> formalParameters) {
        this.formalParameters = formalParameters;
    }*/

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return this.subroutine;
    }

    /**
     * Gets the descriptor of overload of this subroutine matching the specified arguments. Used only in
     * {@link org.guillermomolina.i4gl.parser.identifierstable.types.subroutine.builtin.OverloadedFunctionDescriptor}.
     */
    public SubroutineDescriptor getOverload(List<ExpressionNode> arguments) throws LexicalException {
        return this;
    }

    public List<FormalParameter> getFormalParameters() {
        return this.formalParameters;
    }

    public void setFormalParameters(List<FormalParameter> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public I4GLSubroutine getSubroutine() {
        return this.subroutine;
    }

    public void setRootNode(RootNode rootNode) {
        RootCallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);
        this.subroutine = new I4GLSubroutine(callTarget);
    }

    public I4GLRootNode getRootNode() {
        return (I4GLRootNode) this.subroutine.getCallTarget().getRootNode();
    }

    public boolean hasParameters() {
        return this.formalParameters.size() != 0;
    }


    /**
     * Checks whether this subroutine's parameter at the specified index is a subroutine-type parameter.
     */
    public boolean isSubroutineParameter(int parameterIndex) {
        return this.formalParameters.get(parameterIndex).isSubroutine;
    }

    /**
     * Verifies whether the types of the specified expressions match this subroutine's formal parameters.
     */
    public void verifyArguments(List<ExpressionNode> passedArguments) throws LexicalException {
        if (formalParameters == null) {
            // Forward declared, does not have arguments defined yet
            return;
        }
        if (passedArguments.size() != this.formalParameters.size()) {
            throw new IncorrectNumberOfArgumentsProvidedException(this.formalParameters.size(), passedArguments.size());
        } else {
            for (int i = 0; i < this.formalParameters.size(); ++i) {
                if (!this.formalParameters.get(i).type.equals(passedArguments.get(i).getType()) &&
                        !passedArguments.get(i).getType().convertibleTo(this.formalParameters.get(i).type)) {
                    throw new ArgumentTypeMismatchException(i + 1);
                }
            }
        }
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        if (!(type instanceof SubroutineDescriptor)) {
            return false;
        }

        SubroutineDescriptor subroutine = (SubroutineDescriptor) type;
        return compareFormalParameters(subroutine.formalParameters, this.formalParameters);
    }

    private static boolean compareFormalParameters(List<FormalParameter> left, List<FormalParameter> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < right.size(); ++i) {
            if (!right.get(i).type.equals(left.get(i).type) &&
                    !left.get(i).type.convertibleTo(right.get(i).type)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares the selected lists of formal parameters whether they are equal.
     */
    public static boolean compareFormalParametersExact(List<FormalParameter> left, List<FormalParameter> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < right.size(); ++i) {
            if (!right.get(i).type.equals(left.get(i).type)) {
                return false;
            }
        }

        return true;
    }

}
