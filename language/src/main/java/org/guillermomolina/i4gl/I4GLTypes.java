package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.CharValue;
import org.guillermomolina.i4gl.runtime.customvalues.NullValue;
import org.guillermomolina.i4gl.runtime.customvalues.TextValue;
import org.guillermomolina.i4gl.runtime.customvalues.VarcharValue;

/**
 * The type system of our interpreter. It specifies which variable types we will
 * be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, float.class, double.class, VarcharValue.class, CharValue.class, TextValue.class })
public class I4GLTypes {

    protected I4GLTypes() {
    }

    /**
     * Example of a manually specified type check that replaces the automatically
     * generated type check that the Truffle DSL would generate. For
     * {@link NullValue}, we do not need an {@code instanceof} check, because we
     * know that there is only a {@link NullValue#SINGLETON singleton} instance.
     */
    @TypeCheck(NullValue.class)
    public static boolean isNullValue(Object value) {
        return value == NullValue.SINGLETON;
    }

    /**
     * Example of a manually specified type cast that replaces the automatically
     * generated type cast that the Truffle DSL would generate. For
     * {@link NullValue}, we do not need an actual cast, because we know that there
     * is only a {@link NullValue#SINGLETON singleton} instance.
     */
    @TypeCast(NullValue.class)
    public static NullValue asNullValue(Object value) {
        assert isNullValue(value);
        return NullValue.SINGLETON;
    }

    @ImplicitCast
    public static long castIntToLong(int value) {
        return value;
    }

    @ImplicitCast
    public static float castIntToFloat(int value) {
        return value;
    }

    @ImplicitCast
    public static float castLongToFloat(long value) {
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
    public static double castFloatToDouble(float value) {
        return value;
    }

    @ImplicitCast
    public static TextValue castIntToText(int value) {
        return new TextValue(String.valueOf(value));
    }

    @ImplicitCast
    public static TextValue castLongToText(long value) {
        return new TextValue(String.valueOf(value));
    }

    @ImplicitCast
    public static TextValue castDoubleToText(double value) {
        return new TextValue(String.valueOf(value));
    }
}