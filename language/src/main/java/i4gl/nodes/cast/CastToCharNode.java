package i4gl.nodes.cast;

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
import i4gl.runtime.values.Null;

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
    Object castNull(Null argument) {
        return argument;
    }

    @Specialization(guards = "inputs.isString(argument)", limit = "2")
    Char castText(Object argument, @CachedLibrary("argument") InteropLibrary inputs)  {
        try {
            String asText = inputs.asString(argument);
            Char value = (Char) getCharType().getDefaultValue();
            value.assignString(asText);
            return value;
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, getCharType());
        }
    }
}
