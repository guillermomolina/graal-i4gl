package i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import i4gl.runtime.values.Decimal;
import i4gl.runtime.values.Null;

/**
 *  Type System
 * 
 * <I4GL type> - <type class> - <value class>
 * NULL - {@link NullType} - {@link Null}
 * SMALLINT - {@link SmallIntType} - short
 * INT, INTEGER - {@link IntType} - int
 * BIGINT - {@link BigIntType} - long
 * REAL, SMALLFLOAT - {@link SmallFloatType} - float
 * DOUBLE PRECISION, FLOAT - {@link FloatType} - double
 * DECIMAL, DEC, NUMERIC - {@link DecimalType} - {@link Decimal}
 * DATE - {@link DateType} - {@link Date}
 * TEXT - {@link TextType} - String
 * CHAR - {@link CharType}, {@link Char1Type} - {@link Char}
 * VARCHAR - {@link VarcharType} - {@link Varchar}
 * RECORD - {@link RecordType} - {@link Record}
 * ARRAY - {@link ArrayType} - {@link Array}
 * DATABASE - {@link DatabaseType} - {@link Database}
 * Cursor - {@link CursorType} - {@link Cursor}
 * 
 * Pseudo types:
 *  - {@link LabelType} - {@link Label}
 *  - {@link ReturnType} - Object[]
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
     * {@link Null}, we do not need an {@code instanceof} check, because we know
     * that there is only a {@link Null#SINGLETON singleton} instance.
     */
    @TypeCheck(Null.class)
    public static boolean isNullValue(Object value) {
        return value == Null.SINGLETON;
    }

    /**
     * Example of a manually specified type cast that replaces the automatically
     * generated type cast that the Truffle DSL would generate. For
     * {@link Null}, we do not need an actual cast, because we know that there
     * is only a {@link Null#SINGLETON singleton} instance.
     */
    @TypeCast(Null.class)
    public static Null asNullValue(Object value) {
        if (!isNullValue(value)) {
            throw new AssertionError();
        }
        return Null.SINGLETON;
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
    public static float castDecimalToSmallFloat(Decimal value) {
        return value.toSmallFloat();
    }

    @ImplicitCast
    public static double castDecimalToFloat(Decimal value) {
        return value.toFloat();
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