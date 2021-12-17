package i4gl.nodes.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.values.Null;

public abstract class CastToFloatNode extends UnaryNode {
    
    @Override
    public BaseType getReturnType() {
        return FloatType.SINGLETON;
    }

    @Specialization
    static double castSmallFloat(float argument) {
        return argument;
    }

    @Specialization
    static double castFloat(double argument) {
        return argument;
    }

    @Specialization
    Object castNull(Null argument) {
        return argument;
    }

    @Specialization(guards = "args.fitsInDouble(argument)", limit = "2")
    static double cast(Object argument, @CachedLibrary("argument") InteropLibrary args)  {
        try {
            return args.asDouble(argument);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, FloatType.SINGLETON);
        }
    }

}
