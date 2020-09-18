package org.guillermomolina.i4gl.parser.identifierstable.types.function.builtin;

import java.util.Collections;
import java.util.List;

import com.oracle.truffle.api.frame.FrameDescriptor;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.nodes.ExpressionNode;
import org.guillermomolina.i4gl.nodes.root.I4GLRootNode;
import org.guillermomolina.i4gl.parser.exceptions.ArgumentTypeMismatchException;
import org.guillermomolina.i4gl.parser.exceptions.IncorrectNumberOfArgumentsProvidedException;
import org.guillermomolina.i4gl.parser.exceptions.LexicalException;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.EnumLiteralDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.compound.EnumTypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.function.FunctionDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.BooleanDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.CharDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.LongDescriptor;
import org.guillermomolina.i4gl.parser.utils.FormalParameter;

/**
 * Base type descriptor for type descriptors for I4GL's built-in functions. It additionally to {@link FunctionDescriptor}
 * sets the function's root node in its constructor.
 */
public abstract class BuiltinFunctionDescriptor extends FunctionDescriptor {

    /**
     * The default constructor.
     * @param bodyNode body node of the function
     * @param parameters list of formal parameters of the function
     */
    BuiltinFunctionDescriptor(ExpressionNode bodyNode, List<FormalParameter> parameters) {
        super();
        this.setFormalParameters(formalParameters);
        this.setRootNode(new I4GLRootNode(I4GLLanguage.INSTANCE, new FrameDescriptor(), bodyNode));
    }

    @Override
    public boolean isFunctionParameter(int index) {
        return false;
    }

    /**
     * Specialized built-in function type descriptor for functions with single argument.
     */
    public static class OneArgumentBuiltin extends BuiltinFunctionDescriptor {

        /**
         * The default constructor.
         * @param bodyNode the body node of the function
         * @param parameter the single function's formal parameter
         */
        public OneArgumentBuiltin(ExpressionNode bodyNode, FormalParameter parameter) {
            super(bodyNode, Collections.singletonList(parameter));
        }

        @Override
        public boolean hasParameters() {
            return true;
        }

    }

    /**
     * Specialized built-in function type descriptor for functions with single ordinal-type argument.
     */
    public static class OrdinalArgumentBuiltin extends BuiltinFunctionDescriptor.OneArgumentBuiltin {

        /**
         * The default constructor.
         * @param bodyNode the body node of the function
         * @param parameter the single ordinal-type formal parameter
         */
        OrdinalArgumentBuiltin(ExpressionNode bodyNode, FormalParameter parameter) {
            super(bodyNode, parameter);
        }

        @Override
        public void verifyArguments(List<ExpressionNode> arguments) throws LexicalException {
            if (arguments.size() != 1) {
                throw new IncorrectNumberOfArgumentsProvidedException(1, arguments.size());
            } else {
                TypeDescriptor argumentType = arguments.get(0).getType();
                if (!argumentType.equals(IntDescriptor.getInstance()) && !argumentType.equals(LongDescriptor.getInstance()) &&
                        !argumentType.equals(CharDescriptor.getInstance()) && !argumentType.equals(BooleanDescriptor.getInstance()) &&
                        !(argumentType instanceof EnumLiteralDescriptor) && !(argumentType instanceof EnumTypeDescriptor)) {
                    throw new ArgumentTypeMismatchException(1);
                }
            }
        }
    }

    /**
     * Specialized built-in function type descriptor for functions with zero arguments.
     */
    public static class NoArgumentBuiltin extends BuiltinFunctionDescriptor {

        /**
         * The default constructor.
         * @param bodyNode the body node of the function
         */
        public NoArgumentBuiltin(ExpressionNode bodyNode) {
            super(bodyNode, Collections.emptyList());
        }

    }

}
