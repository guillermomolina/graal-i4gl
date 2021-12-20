package i4gl.nodes.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.Char1Type;
import i4gl.runtime.values.Null;

public abstract class CastToChar1Node extends UnaryNode {

    @Override
    public BaseType getReturnType() {
        return Char1Type.SINGLETON;
    }

    @Specialization
    static char castChar1(char argument) {
        return argument;
    }

    @Specialization
    static char castText(String argument) {
        if (argument.isEmpty()) {
            return 0;
        }
        return argument.charAt(0);
    }

    @Specialization
    static Object castNull(Null argument) {
        return argument;
    }

    @Specialization(guards = "args.fitsInByte(argument)", limit = "2")
    static char castInt(Object argument, @CachedLibrary("argument") InteropLibrary args) {
        try {
            int value = args.asByte(argument);
            return (char) value;
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, Char1Type.SINGLETON);
        }
    }

    @Specialization(guards = "inputs.isString(argument)", limit = "2")
    static char castObject(Object argument, @CachedLibrary("argument") InteropLibrary inputs) {
        try {
            String asText = inputs.asString(argument);
            return castText(asText);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, Char1Type.SINGLETON);
        }
    }
}
