package i4gl.nodes.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.values.Null;

public abstract class CastToBigIntNode extends UnaryNode {
    
    @Override
    public BaseType getReturnType() {
        return BigIntType.SINGLETON;
    }

    @Specialization
    static long castSmallInt(short argument) {
        return argument;
    }

    @Specialization
    static long castInt(int argument) {
        return argument;
    }

    @Specialization
    static long castBigInt(long argument) {
        return argument;
    }

    @Specialization
    static long castText(String argument) {
        return Long.valueOf(argument);
    }

    @Specialization
    static Object castNull(Null argument) {
        return argument;
    }

    @Specialization(guards = "args.fitsInLong(argument)", limit = "2")
    static long cast(Object argument, @CachedLibrary("argument") InteropLibrary args) {
        try {
            return args.asLong(argument);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, BigIntType.SINGLETON);
        }
    }
}
