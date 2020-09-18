package org.guillermomolina.i4gl.parser.identifierstable.types.function;

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
import org.guillermomolina.i4gl.runtime.customvalues.I4GLFunction;

/**
 * Base type descriptor for functions. It contains the list of its formal parameters and an instance of {@link I4GLFunction}
 * representing the function.
 */
public class FunctionDescriptor implements TypeDescriptor {

    protected List<FormalParameter> formalParameters;

    protected I4GLFunction function;

    /**
     * The default constructor.
     * @param formalParameters list of the function's formal parameters
     */
    public FunctionDescriptor() {
        this.formalParameters = null;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return this.function;
    }

    /**
     * Gets the descriptor of overload of this function matching the specified arguments. Used only in
     * {@link org.guillermomolina.i4gl.parser.identifierstable.types.function.builtin.OverloadedFunctionDescriptor}.
     */
    @SuppressWarnings("unused")
    public FunctionDescriptor getOverload(List<ExpressionNode> arguments) {
        return this;
    }

    public List<FormalParameter> getFormalParameters() {
        return this.formalParameters;
    }

    public void setFormalParameters(List<FormalParameter> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public I4GLFunction getFunction() {
        return this.function;
    }

    public void setRootNode(RootNode rootNode) {
        RootCallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);
        this.function = new I4GLFunction(callTarget);
    }

    public I4GLRootNode getRootNode() {
        return (I4GLRootNode) this.function.getCallTarget().getRootNode();
    }

    public boolean hasParameters() {
        return this.formalParameters.size() != 0;
    }


    /**
     * Checks whether this function's parameter at the specified index is a function-type parameter.
     */
    public boolean isFunctionParameter(int parameterIndex) {
        return this.formalParameters.get(parameterIndex).isFunction;
    }

    /**
     * Verifies whether the types of the specified expressions match this function's formal parameters.
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
        if (!(type instanceof FunctionDescriptor)) {
            return false;
        }

        FunctionDescriptor functionDescriptor = (FunctionDescriptor) type;
        return compareFormalParameters(functionDescriptor.formalParameters, this.formalParameters);
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
