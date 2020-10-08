package org.guillermomolina.i4gl.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;

@ExportLibrary(value = InteropLibrary.class, delegateTo = "delegate")
public final class I4GLLanguageView implements TruffleObject {

    final Object delegate;

    I4GLLanguageView(Object delegate) {
        this.delegate = delegate;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    /*
     * Language views must always associate with the language they were created for.
     * This allows tooling to take a primitive or foreign value and create a value
     * of i4gl of it.
     */
    @ExportMessage
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    @ExplodeLoop
    boolean hasMetaObject(@CachedLibrary("this.delegate") InteropLibrary interop) {
        /*
         * We use the isInstance method to find out whether one of the builtin i4gl
         * types apply. If yes, then we can provide a meta object in getMetaObject. The
         * interop contract requires to be precise.
         *
         * Since language views are only created for primitive values and values of
         * other languages, values from i4gl itself directly implement
         * has/getMetaObject. For example I4GLFunction is already associated with the
         * I4GLLanguage and therefore the language view will not be used.
         */
        for (I4GLType type : I4GLType.PRECEDENCE) {
            if (type.isInstance(delegate, interop)) {
                return true;
            }
        }
        return false;
    }

    @ExportMessage
    @ExplodeLoop
    Object getMetaObject(@CachedLibrary("this.delegate") InteropLibrary interop) throws UnsupportedMessageException {
        /*
         * We do the same as in hasMetaObject but actually return the type this time.
         */
        for (I4GLType type : I4GLType.PRECEDENCE) {
            if (type.isInstance(delegate, interop)) {
                return type;
            }
        }
        throw UnsupportedMessageException.create();
    }

    @ExportMessage
    @ExplodeLoop
    Object toDisplayString(boolean allowSideEffects, @CachedLibrary("this.delegate") InteropLibrary interop) {
        for (I4GLType type : I4GLType.PRECEDENCE) {
            if (type.isInstance(this.delegate, interop)) {
                try {
                    /*
                     * The type is a partial evaluation constant here as we use @ExplodeLoop. So
                     * this if-else cascade should fold after partial evaluation.
                     */
                    if (type == I4GLType.INT) {
                        return intToString(interop.asInt(delegate));
                    } else if (type == I4GLType.BIGINT) {
                        return longToString(interop.asLong(delegate));
                    } else if (type == I4GLType.SMALLFLOAT) {
                        return floatToString(interop.asFloat(delegate));
                    } else if (type == I4GLType.DOUBLE) {
                        return doubleToString(interop.asDouble(delegate));
                    } else if (type == I4GLType.TEXT) {
                        return addQuotes(interop.asString(delegate));
                    } else {
                        /* We use the type name as fallback for any other type */
                        return type.getName();
                    }
                } catch (UnsupportedMessageException e) {
                    CompilerDirectives.transferToInterpreter();
                    throw new AssertionError();
                }
            }
        }
        return "Unsupported";
    }

    private static String addQuotes(String text) {
        return '"' + text + '"';
    }

    @TruffleBoundary
    private static String intToString(int i) {
        return Integer.toString(i);
    }

    @TruffleBoundary
    private static String longToString(long l) {
        return Long.toString(l);
    }

    @TruffleBoundary
    private static String floatToString(float f) {
        return Float.toString(f);
    }

    @TruffleBoundary
    private static String doubleToString(double d) {
        return Double.toString(d);
    }

    public static Object create(Object value) {
        if (!isPrimitiveOrFromOtherLanguage(value)) {
            throw new AssertionError();
        }
        return new I4GLLanguageView(value);
    }

    /*
     * Language views are intended to be used only for primitives and other language
     * values.
     */
    private static boolean isPrimitiveOrFromOtherLanguage(Object value) {
        InteropLibrary interop = InteropLibrary.getFactory().getUncached(value);
        try {
            return !interop.hasLanguage(value) || interop.getLanguage(value) != I4GLLanguage.class;
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError(e);
        }
    }

    /**
     * Returns a language view for primitive or foreign values. Returns the same
     * value for values that are already originating from SimpleLanguage. This is
     * useful to view values from the perspective of i4gl in slow paths, for
     * example, printing values in error messages.
     */
    @TruffleBoundary
    public static Object forValue(Object value) {
        if (value == null) {
            return null;
        }
        InteropLibrary lib = InteropLibrary.getFactory().getUncached(value);
        try {
            if (lib.hasLanguage(value) && lib.getLanguage(value) == I4GLLanguage.class) {
                return value;
            } else {
                /*if (value.getClass().isArray()) {
                    return new ArrayValue(value);
                }*/
                return create(value);
            }
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError(e);
        }
    }
}
