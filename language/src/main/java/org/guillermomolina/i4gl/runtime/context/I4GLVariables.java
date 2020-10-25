package org.guillermomolina.i4gl.runtime.context;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.types.primitive.I4GLObjectType;

@ExportLibrary(InteropLibrary.class)
final class I4GLVariables implements TruffleObject {

    final Map<String, Object> variables = new HashMap<>();

    I4GLVariables() {
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    @TruffleBoundary
    Object readMember(String member) {
        return variables.get(member);
    }

    @ExportMessage
    @TruffleBoundary
    boolean isMemberReadable(String member) {
        return variables.containsKey(member);
    }

    @ExportMessage
    @TruffleBoundary
    Object getMembers(boolean includeInternal) {
        return new VariableNamesObject(variables.keySet().toArray());
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return I4GLObjectType.SINGLETON;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return variables.toString();
    }

    public static boolean isInstance(TruffleObject obj) {
        return obj instanceof I4GLVariables;
    }

    @ExportLibrary(InteropLibrary.class)
    static final class VariableNamesObject implements TruffleObject {

        private final Object[] names;

        VariableNamesObject(Object[] names) {
            this.names = names;
        }

        @ExportMessage
        boolean hasArrayElements() {
            return true;
        }

        @ExportMessage
        boolean isArrayElementReadable(long index) {
            return index >= 0 && index < names.length;
        }

        @ExportMessage
        long getArraySize() {
            return names.length;
        }

        @ExportMessage
        Object readArrayElement(long index) throws InvalidArrayIndexException {
            if (!isArrayElementReadable(index)) {
                CompilerDirectives.transferToInterpreter();
                throw InvalidArrayIndexException.create(index);
            }
            return names[(int) index];
        }
    }
}
