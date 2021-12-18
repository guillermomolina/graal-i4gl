package i4gl.nodes.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.values.Null;

public abstract class CastToTextNode extends UnaryNode {

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

    @Specialization
    Object castNull(Null argument) {
        // NOTE: in i4gl a TEXT can not be NULL 
        return argument;
    }

    @Specialization(guards = "inputs.isString(argument)", limit = "2")
    String castText(Object argument, @CachedLibrary("argument") InteropLibrary inputs) {
        try {
            return inputs.asString(argument);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, TextType.SINGLETON);
        }
    }
}
