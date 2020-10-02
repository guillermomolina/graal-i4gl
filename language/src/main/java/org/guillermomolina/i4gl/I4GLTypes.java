package org.guillermomolina.i4gl;

import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import org.guillermomolina.i4gl.runtime.customvalues.NullValue;

/**
 * The type system of our interpreter. It specifies which variable types we will
 * be using and implicit casts.
 */
@TypeSystem({  })
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
}