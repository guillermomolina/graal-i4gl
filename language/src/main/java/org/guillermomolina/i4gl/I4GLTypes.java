package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.NullValue;

/**
 * I4GL Type System
 * INT, INTEGER - IntDescriptor - int
 * BIGINT - BigIntDescriptor - long
 * REAL, SMALLFLOAT - SmallFloatDescriptor - float
 * DOUBLE PRECISION, FLOAT - DoubleDescriptor - double
 * TEXT - TextDescriptor - String
 * CHAR - CharDescriptor, Char1Descriptor - CharValue
 * VARCHAR - VarcharDescriptor - VarcharValue
 * RECORD - RecordDescriptor - RecordValue
 * ARRAY - ArrayDescriptor - ArrayValue
 * 
 * Pseudo types:
 *  - LabelDescriptor - 
 *  - ReturnDescriptor - Object[]
 * NULL - - NullValue
 * DATABASE - DatabaseDescriptor - DatabaseValue
 */

/**
 * The type system of our interpreter. It specifies which variable types we will
 * be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, float.class, double.class })
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
    public static long castIntToBigInt(int value) {
        return value;
    }

    @ImplicitCast
    public static float castIntToSmallFloat(int value) {
        return value;
    }

    @ImplicitCast
    public static float castBigIntToSmallFloat(long value) {
        return value;
    }

    @ImplicitCast
    public static double castIntToFloat(int value) {
        return value;
    }

    @ImplicitCast
    public static double castBigIntToFloat(long value) {
        return value;
    }

    @ImplicitCast
    public static double castSmallFloatToFloat(float value) {
        return value;
    }

    @ImplicitCast
    public static String castIntToText(int value) {
        return String.valueOf(value);
    }

    @ImplicitCast
    public static String castBigIntToText(long value) {
        return String.valueOf(value);
    }

    @ImplicitCast
    public static String castFloatToText(double value) {
        return String.valueOf(value);
    }
}