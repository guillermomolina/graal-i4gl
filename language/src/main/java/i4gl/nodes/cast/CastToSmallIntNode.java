package i4gl.nodes.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.SmallIntType;

public abstract class CastToSmallIntNode extends UnaryNode {
    
    @Override
    public BaseType getReturnType() {
        return SmallIntType.SINGLETON;
    }

    @Specialization
    static short castSmallInt(short argument) {
        return argument;
    }

    @Specialization(guards = "args.fitsInShort(argument)", limit = "2")
    static short cast(Object argument, @CachedLibrary("argument") InteropLibrary args)  {
        try {
            return args.asShort(argument);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, SmallIntType.SINGLETON);
        }
    }

}
