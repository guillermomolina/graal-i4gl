package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.*;
import org.guillermomolina.i4gl.runtime.customvalues.I4GLArray;

/**
 * The type system of our interpreter. It specifies which variable types we will be using and implicit casts.
 */
@TypeSystem({ int.class, long.class, boolean.class, char.class, double.class, I4GLString.class,
        I4GLArray.class, Reference.class, PointerValue.class, SetTypeValue.class, FileValue.class, })
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
    public static I4GLString castCharToString(char c) {
        return new I4GLString(String.valueOf(c));
    }
    
}