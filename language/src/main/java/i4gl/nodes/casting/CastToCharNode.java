package i4gl.nodes.casting;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;

import i4gl.exceptions.InvalidCastException;
import i4gl.nodes.expression.UnaryNode;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Char;

@NodeField(name = "charType", type = BaseType.class)
public abstract class CastToCharNode extends UnaryNode {

    protected abstract BaseType getCharType();

    @Override
    public BaseType getReturnType() {
        return getCharType();
    }

    @Specialization
    Char castChar1(char argument) {
        Char value = (Char) getCharType().getDefaultValue();
        value.assignString(String.valueOf(argument));
        return value;
    }

    @Specialization
    Char castText(String argument) {
        Char value = (Char) getCharType().getDefaultValue();
        value.assignString(argument);
        return value;
    }

    @Specialization(guards = "args.fitsInShort(argument)", limit = "2")
    Char cast(Object argument, @CachedLibrary("argument") InteropLibrary args) {
        try {
            return castText(args.asString(argument));
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, getCharType());
        }
    }
}
