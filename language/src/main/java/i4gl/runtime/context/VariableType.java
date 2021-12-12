package i4gl.runtime.context;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;

@ExportLibrary(InteropLibrary.class)
final class VariableType implements TruffleObject {

    public static final VariableType SINGLETON = new VariableType();

    VariableType() {
    }

    @Override
    public String toString() {
        return "VARIABLE";
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<Context>> getLanguage() {
        return I4GLLanguage.class;
    }

    /*
     * All I4GLTypeSystem are declared as interop meta-objects. Other example for
     * meta-objects are Java classes, or JavaScript prototypes.
     */
    @ExportMessage
    boolean isMetaObject() {
        return false;
    }

    @ExportMessage
    boolean isMetaInstance(Object instance) throws UnsupportedMessageException {
        return false;
    }

    /*
     * I4GL does not have the notion of a qualified or simple name, so we return the
     * same type name for both.
     */
    @ExportMessage(name = "getMetaQualifiedName")
    @ExportMessage(name = "getMetaSimpleName")
    public Object getName() {
        return toString();
    }

    @ExportMessage(name = "toDisplayString")
    Object toDisplayString(boolean allowSideEffects) {
        return toString();
    }
}
