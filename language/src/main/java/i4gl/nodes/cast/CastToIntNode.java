package i4gl.nodes.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.Null;

public abstract class CastToIntNode extends UnaryNode {
    
    @Override
    public BaseType getReturnType() {
        return IntType.SINGLETON;
    }

    @Specialization
    static int castSmallInt(short argument) {
        return argument;
    }

    @Specialization
    static int castInt(int argument) {
        return argument;
    }

    @Specialization
    static int castText(String argument) {
        return Integer.valueOf(argument);
    }

    @Specialization
    static Object castNull(Null argument) {
        return argument;
    }

    @Specialization(guards = "args.fitsInInt(argument)", limit = "2")
    static int cast(Object argument, @CachedLibrary("argument") InteropLibrary args) {
        try {
            return args.asInt(argument);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, IntType.SINGLETON);
        }
    }

}
