package org.guillermomolina.i4gl.runtime.customvalues;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class RecordValue implements TruffleObject {

    private final Map<String, Object> properties;

    public RecordValue(Map<String, Object> properties) {
        this.properties = properties;
    }

    public RecordValue(RecordValue source) {
        this.properties = source.properties;
    }

    public Object get(String name) {
        return properties.get(name);
    }

    public void put(String name, Object value) {
        properties.put(name, value);
    }

    public Object createDeepCopy() {
        return new RecordValue(this);
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage(name = "isMemberReadable")
    @ExportMessage(name = "isMemberModifiable")
    @ExportMessage(name = "isMemberRemovable")
    @TruffleBoundary
    boolean hasMember(String name) {
        return properties.containsKey(name);
    }

    @ExportMessage
    boolean isMemberInsertable(String name) {
        return !hasMember(name);
    }

    @ExportMessage
    @TruffleBoundary
    Object readMember(String name) throws UnknownIdentifierException {
        Object value = properties.get(name);
        if (value == null) {
            throw UnknownIdentifierException.create(name);
        }
        return value;
    }

    @ExportMessage
    @TruffleBoundary
    void writeMember(String name, Object value) {
        properties.put(name, value);
    }

    @ExportMessage
    @TruffleBoundary
    void removeMember(String name) {
        properties.remove(name);
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
    @TruffleBoundary
    Object getMembers(boolean includeInternal) {
        return new RecordNamesObject(properties.keySet().toArray());
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return I4GLType.RECORD;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return properties.toString();
    }

    public static boolean isInstance(TruffleObject obj) {
        return obj instanceof RecordValue;
    }

    @ExportLibrary(InteropLibrary.class)
    static final class RecordNamesObject implements TruffleObject {

        private final Object[] names;

        RecordNamesObject(Object[] names) {
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
