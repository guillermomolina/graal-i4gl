package org.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.values.I4GLNull;

/**
 * I4GL Type System
 * INT, INTEGER - I4GLIntType - int
 * BIGINT - I4GLBigIntType - long
 * REAL, SMALLFLOAT - I4GLSmallFloatType - float
 * DOUBLE PRECISION, FLOAT - I4GLFloatType - double
 * TEXT - I4GLTextType - String
 * CHAR - I4GLCharType, Char1Type - I4GLChar
 * VARCHAR - I4GLVarcharType - I4GLVarchar
 * RECORD - I4GLRecordType - I4GLRecord
 * ARRAY - I4GLArrayType - I4GLIntArray, I4GLBigIntArray, I4GLSmallFloatArray, I4GLFloatArray
 * 
 * Pseudo types:
 *  - I4GLLabelType - I4GLLabel
 *  - I4GLReturnType - Object[]
 * NULL - I4GLNullType - I4GLNull
 * DATABASE - I4GLDatabaseType - I4GLDatabase
 */

/**
 * The type system of our interpreter. It specifies which variable types we will
 * be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, float.class, double.class, String.class })
public class I4GLTypeSystem {

    protected I4GLTypeSystem() {
    }

    /**
     * Example of a manually specified type check that replaces the automatically
     * generated type check that the Truffle DSL would generate. For
     * {@link I4GLNull}, we do not need an {@code instanceof} check, because we
     * know that there is only a {@link I4GLNull#SINGLETON singleton} instance.
     */
    @TypeCheck(I4GLNull.class)
    public static boolean isNullValue(Object value) {
        return value == I4GLNull.SINGLETON;
    }

    /**
     * Example of a manually specified type cast that replaces the automatically
     * generated type cast that the Truffle DSL would generate. For
     * {@link I4GLNull}, we do not need an actual cast, because we know that there
     * is only a {@link I4GLNull#SINGLETON singleton} instance.
     */
    @TypeCast(I4GLNull.class)
    public static I4GLNull asNullValue(Object value) {
        if(!isNullValue(value)) {
            throw new AssertionError();
        }
        return I4GLNull.SINGLETON;
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