package org.guillermomolina.i4gl.nodes;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.values.I4GLNull;

/**
 * I4GL Type System
 * 
 * <I4GL type> - <type class> - <value class>
 * NULL - {@link I4GLNullType} - {@link I4GLNull}
 * SMALLINT - {@link I4GLSmallIntType} - short
 * INT, INTEGER - {@link I4GLIntType} - int
 * BIGINT - {@link I4GLBigIntType} - long
 * REAL, SMALLFLOAT - {@link I4GLSmallFloatType} - float
 * DOUBLE PRECISION, FLOAT - {@link I4GLFloatType} - double
 * TEXT - {@link I4GLTextType} - String
 * CHAR - {@link I4GLCharType}, {@link Char1Type} - {@link I4GLChar}
 * VARCHAR - {@link I4GLVarcharType} - {@link I4GLVarchar}
 * RECORD - {@link I4GLRecordType} - {@link I4GLRecord}
 * ARRAY - {@link I4GLArrayType} - {@link I4GLSmallIntArray}, {@link I4GLIntArray}, {@link I4GLBigIntArray}, 
 *                                  {@link I4GLSmallFloatArray}, {@link I4GLFloatArray}
 * DATABASE - {@link I4GLDatabaseType} - {@link I4GLDatabase}
 * Cursor - {@link I4GLCursorType} - {@link I4GLCursor}
 * 
 * Pseudo types:
 *  - {@link I4GLLabelType} - {@link I4GLLabel}
 *  - {@link I4GLReturnType} - Object[]
 */

/**
 * The type system of our interpreter. It specifies which variable types we will
 * be using and implicit casts.
 */
@TypeSystem({ short.class, int.class, long.class, float.class, double.class, String.class })
public class I4GLTypeSystem {

    protected I4GLTypeSystem() {
    }

    /**
     * Example of a manually specified type check that replaces the automatically
     * generated type check that the Truffle DSL would generate. For
     * {@link I4GLNull}, we do not need an {@code instanceof} check, because we know
     * that there is only a {@link I4GLNull#SINGLETON singleton} instance.
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
        if (!isNullValue(value)) {
            throw new AssertionError();
        }
        return I4GLNull.SINGLETON;
    }

    @ImplicitCast
    public static int castShortToInt(short value) {
        return value;
    }

    @ImplicitCast
    public static long castIntToBigInt(int value) {
        return value;
    }

    @ImplicitCast
    public static float castSmallIntToSmallFloat(short value) {
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
    public static double castSmallFloatToFloat(float value) {
        return value;
    }

    @ImplicitCast
    public static String castShortToText(short value) {
        return String.format("%6d", value);
    }

    @ImplicitCast
    public static String castIntToText(int value) {
        return String.format("%11d", value);
    }

    @ImplicitCast
    public static String castBigIntToText(long value) {
        return String.format("%20d", value);
    }

    @ImplicitCast
    public static String castSmallFloatToText(float value) {
        return String.format("%14.2f", value);
    }

    @ImplicitCast
    public static String castFloatToText(double value) {
        return String.format("%14.2f", value);
    }
}