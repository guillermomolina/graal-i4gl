package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.FileValue;
import org.guillermomolina.i4gl.runtime.customvalues.ArrayValue;
import org.guillermomolina.i4gl.runtime.customvalues.TextValue;

/**
 * The type system of our interpreter. It specifies which variable types we will be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, boolean.class, char.class, double.class, TextValue.class,
        ArrayValue.class, FileValue.class})
public class I4GLTypes {

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

    @ImplicitCast
    public static TextValue castCharToString(char c) {
        return new TextValue(String.valueOf(c));
    }
/*
    @ImplicitCast
    public static int castStringToInt(Text value) {
        try {
            return Integer.parseInt(value.toString());
        } catch(final NumberFormatException e) {
            return 0;
        }       
    }

    @ImplicitCast
    public static long castStringToLong(Text value) {
        try {
            return Long.parseLong(value.toString());
        } catch(final NumberFormatException e) {
            return 0;
        }       
    }

    @ImplicitCast
    public static double castStringToDouble(Text value) {
        try {
            return Double.parseDouble(value.toString());
        } catch(final NumberFormatException e) {
            return Double.NaN;
        }       
    }
*/
}