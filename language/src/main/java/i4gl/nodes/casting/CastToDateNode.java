package i4gl.nodes.casting;

import java.text.ParseException;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.values.Date;

public abstract class CastToDateNode extends UnaryNode {

    @Override
    public BaseType getReturnType() {
        return DateType.SINGLETON;
    }


    @Specialization
    Date castText(String argument) {
        try {
            return Date.valueOf(argument);
        } catch (ParseException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, DateType.SINGLETON);
        }
    }

    @Specialization(guards = "args.fitsInShort(argument)", limit = "2")
    Date cast(Object argument, @CachedLibrary("argument") InteropLibrary args) {
        try {
            return castText(args.asString(argument));
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, DateType.SINGLETON);
        }
    }
}
