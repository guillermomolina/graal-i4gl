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
import i4gl.runtime.values.Varchar;

@NodeField(name = "varcharType", type = BaseType.class)
public abstract class CastToVarcharNode extends UnaryNode {

    protected abstract BaseType getVarcharType();

    @Override
    public BaseType getReturnType() {
        return getVarcharType();
    }

    @Specialization
    Varchar castText(String argument) {
        Varchar value = (Varchar) getVarcharType().getDefaultValue();
        value.assignString(argument);
        return value;
    }

    @Specialization(guards = "args.fitsInShort(argument)", limit = "2")
    Varchar cast(Object argument, @CachedLibrary("argument") InteropLibrary args) {
        try {
            return castText(args.asString(argument));
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, getVarcharType());
        }
    }
}
