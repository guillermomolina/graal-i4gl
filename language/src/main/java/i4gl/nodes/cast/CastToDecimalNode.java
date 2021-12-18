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
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.values.Decimal;
import i4gl.runtime.values.Null;

@NodeField(name = "decimalType", type = BaseType.class)
public abstract class CastToDecimalNode extends UnaryNode {
    
    protected abstract BaseType getDecimalType();

    @Override
    public BaseType getReturnType() {
        return getDecimalType();
    }

    @Specialization
    Decimal castSmallInt(short argument) {
        return new Decimal(argument);
    }

    @Specialization
    Decimal castInt(int argument) {
        return new Decimal(argument);
    }

    @Specialization
    Decimal castBigInt(long argument) {
        return new Decimal(argument);
    }

    @Specialization
    Decimal castSmallFloat(float argument) {
        return new Decimal(argument);
    }

    @Specialization
    Decimal castFloat(double argument) {
        return new Decimal(argument);
    }

    @Specialization
    Object castNull(Null argument) {
        return argument;
    }

    @Specialization(guards = "args.fitsInDouble(argument)", limit = "2")
    Decimal cast(Object argument, @CachedLibrary("argument") InteropLibrary args)  {
        try {
            return new Decimal(args.asDouble(argument));
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            throw new InvalidCastException(argument, FloatType.SINGLETON);
        }
    }

}
