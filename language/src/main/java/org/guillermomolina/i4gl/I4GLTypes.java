package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.ArrayValue;
import org.guillermomolina.i4gl.runtime.customvalues.CharValue;
import org.guillermomolina.i4gl.runtime.customvalues.TextValue;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * The type system of our interpreter. It specifies which variable types we will
 * be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, float.class, double.class, TextValue.class, VarcharValue.class, CharValue.class,
        ArrayValue.class })
public class I4GLTypes {

    protected I4GLTypes() {
    }

    @ImplicitCast
    public static long castIntToLong(int value) {
        return value;
    }

    @ImplicitCast
    public static double castIntToDouble(int value) {
        return value;
    }

    @ImplicitCast
    public static double castLongToDouble(long value) {
        return value;
    }

    @ImplicitCast
    public static TextValue castIntToString(int value) {
        return new TextValue(String.valueOf(value));
    }

    @ImplicitCast
    public static TextValue castLongToString(long value) {
        return new TextValue(String.valueOf(value));
    }

    @ImplicitCast
    public static TextValue castDoubleToString(double value) {
        return new TextValue(String.valueOf(value));
    }
}