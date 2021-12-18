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
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Decimal;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.Varchar;

public abstract class CastToTextNode extends UnaryNode {

    @Override
    public BaseType getReturnType() {
        return TextType.SINGLETON;
    }

    @Specialization
    String castSmallInt(short argument) {
        return String.format("%6d", argument);
    }

    @Specialization
    String castInt(int argument) {
        return String.format("%11d", argument);
    }

    @Specialization
    String castBigInt(long argument) {
        return String.format("%20d", argument);
    }

    @Specialization
    String castDecimal(Decimal argument) {
        return argument.toString();
    }

    @Specialization
    String castSmallFloat(float argument) {
        return String.format("%14.2f", argument);
    }

    @Specialization
    String castFloat(double argument) {
        return String.format("%14.2f", argument);
    }

    @Specialization
    String castChar(Char argument) {
        return argument.toString();
    }

    @Specialization
    String castVarchar(Varchar argument) {
        return argument.toString();
    }

    @Specialization
    Object castNull(Null argument) {
        // NOTE: in i4gl a TEXT can not be NULL
        return argument;
    }

    @Specialization
    String castText(String argument) {
        return argument;
    }

    @Specialization(guards = "inputs.isString(argument)", limit = "2")
    String castObject(Object argument, @CachedLibrary("argument") InteropLibrary inputs) {
        try {
            return inputs.asString(argument);
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, TextType.SINGLETON);
        }
    }
}
