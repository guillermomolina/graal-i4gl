package i4gl.runtime.values;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.utilities.TriState;

import i4gl.I4GLLanguage;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.types.primitive.NullType;

@ExportLibrary(InteropLibrary.class)
public final class I4GLNull implements TruffleObject {

    /**
     * The canonical value to represent {@code null} in I4GL.
     */
    public static final I4GLNull SINGLETON = new I4GLNull();
    private static final int IDENTITY_HASH = System.identityHashCode(SINGLETON);

    /**
     * Disallow instantiation from outside to ensure that the {@link #SINGLETON} is the only
     * instance.
     */
    private I4GLNull() {
    }

    @Override
    public String toString() {
        return "";
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return toString();
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
        return I4GLLanguage.class;
    }

    /**
     * {@link I4GLNull} values are interpreted as null values by other languages.
     */
    @ExportMessage
    boolean isNull() {
        return true;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return NullType.SINGLETON;
    }

    @ExportMessage
    static TriState isIdenticalOrUndefined(I4GLNull receiver, Object other) {
        /*
         * NullValue values are identical to other NullValue values.
         */
        return TriState.valueOf(I4GLNull.SINGLETON == other);
    }

    @ExportMessage
    static int identityHashCode(I4GLNull receiver) {
        /*
         * We do not use 0, as we want consistency with System.identityHashCode(receiver).
         */
        return IDENTITY_HASH;
    }
    
}
