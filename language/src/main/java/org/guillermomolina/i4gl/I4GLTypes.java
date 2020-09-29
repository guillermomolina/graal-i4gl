package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.FileValue;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLArray;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLString;

/**
 * The type system of our interpreter. It specifies which variable types we will be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, boolean.class, char.class, double.class, I4GLString.class,
        I4GLArray.class, FileValue.class})
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
    public static I4GLString castIntToString(int value) {
        return new I4GLString(String.valueOf(value));
    }

    @ImplicitCast
    public static I4GLString castLongToString(long value) {
        return new I4GLString(String.valueOf(value));
    }

    @ImplicitCast
    public static I4GLString castDoubleToString(double value) {
        return new I4GLString(String.valueOf(value));
    }

    @ImplicitCast
    public static I4GLString castCharToString(char c) {
        return new I4GLString(String.valueOf(c));
    }
/*
    @ImplicitCast
    public static int castStringToInt(I4GLString value) {
        try {
            return Integer.parseInt(value.toString());
        } catch(final NumberFormatException e) {
            return 0;
        }       
    }

    @ImplicitCast
    public static long castStringToLong(I4GLString value) {
        try {
            return Long.parseLong(value.toString());
        } catch(final NumberFormatException e) {
            return 0;
        }       
    }

    @ImplicitCast
    public static double castStringToDouble(I4GLString value) {
        try {
            return Double.parseDouble(value.toString());
        } catch(final NumberFormatException e) {
            return Double.NaN;
        }       
    }
*/
}