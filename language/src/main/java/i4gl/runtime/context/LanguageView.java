package i4gl.runtime.context;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import i4gl.I4GLLanguage;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.DateType;
import i4gl.runtime.types.compound.TextType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;

@ExportLibrary(value = InteropLibrary.class, delegateTo = "delegate")
public final class LanguageView implements TruffleObject {

    final Object delegate;
    /*
     * This array is used when all types need to be checked in a certain order.
     * While most interop types like number or string are exclusive, others traits
     * like members might not be. For example, an object might be a function.
     */
    @CompilationFinal(dimensions = 1)
    protected static final BaseType[] PRECEDENCE = {
        SmallIntType.SINGLETON, IntType.SINGLETON, BigIntType.SINGLETON, SmallFloatType.SINGLETON, FloatType.SINGLETON,
            TextType.SINGLETON, DateType.SINGLETON };

    LanguageView(Object delegate) {
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
    Class<? extends TruffleLanguage<Context>> getLanguage() {
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
        for (BaseType type : PRECEDENCE) {
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
        for (BaseType type : PRECEDENCE) {
            if (type.isInstance(delegate, interop)) {
                return type;
            }
        }
        throw UnsupportedMessageException.create();
    }

    @ExportMessage
    @ExplodeLoop
    Object toDisplayString(boolean allowSideEffects, @CachedLibrary("this.delegate") InteropLibrary interop) {
        for (BaseType type : PRECEDENCE) {
            if (type.isInstance(this.delegate, interop)) {
                try {
                    /*
                     * The type is a partial evaluation constant here as we use @ExplodeLoop. So
                     * this if-else cascade should fold after partial evaluation.
                     */
                    Object typeName = type.getName();
                    if (type == SmallIntType.SINGLETON) {
                        return typeName + " " + smallIntToString((short)interop.asInt(delegate));
                    } else if (type == IntType.SINGLETON) {
                        return typeName + " " + intToString(interop.asInt(delegate));
                    } else if (type == BigIntType.SINGLETON) {
                        return typeName + " " + bigIntToString(interop.asLong(delegate));
                    } else if (type == SmallFloatType.SINGLETON) {
                        return typeName + " " + smallFloatToString(interop.asFloat(delegate));
                    } else if (type == FloatType.SINGLETON) {
                        return typeName + " " + doubleToString(interop.asDouble(delegate));
                    } else if (type == TextType.SINGLETON) {
                        return typeName + " " + addQuotes(interop.asString(delegate));
                    } else {
                        return typeName;
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
    private static String smallIntToString(short s) {
        return Short.toString(s);
    }

    @TruffleBoundary
    private static String intToString(int i) {
        return Integer.toString(i);
    }

    @TruffleBoundary
    private static String bigIntToString(long l) {
        return Long.toString(l);
    }

    @TruffleBoundary
    private static String smallFloatToString(float f) {
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
        return new LanguageView(value);
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
                return create(value);
            }
        } catch (UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw new AssertionError(e);
        }
    }
}
