package i4gl.runtime.values;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.runtime.context.Context;
import i4gl.runtime.exceptions.InvalidCastException;
import i4gl.runtime.types.compound.Char1Type;
import i4gl.runtime.types.compound.RecordType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;

@ExportLibrary(InteropLibrary.class)
public class Record implements TruffleObject {

    private final RecordType recordType;
    private final Map<String, Object> properties;

    public Record(final RecordType recordType, Map<String, Object> properties) {
        this.recordType = recordType;
        this.properties = properties;
    }

    public Record(Record source) {
        this.recordType = source.recordType;
        this.properties = source.properties;
    }

    public boolean isChar(String name) {
        final Object value = properties.get(name);
        return value instanceof Byte;
    }

    public char getCharSafe(String name) throws InvalidCastException {
        final Object value = properties.get(name);
        try {
            return (char) value;
        } catch (ClassCastException ex) {
            throw new InvalidCastException(value, Char1Type.SINGLETON);
        }
    }

    public boolean isSmallInt(String name) {
        final Object value = properties.get(name);
        return value instanceof Short;
    }

    public short getSmallIntSafe(String name) throws InvalidCastException {
        final Object value = properties.get(name);
        try {
            return (short) value;
        } catch (ClassCastException ex) {
            throw new InvalidCastException(value, SmallIntType.SINGLETON);
        }
    }

    public boolean isInt(String name) {
        final Object value = properties.get(name);
        return value instanceof Integer;
    }

    public int getIntSafe(String name) throws InvalidCastException {
        final Object value = properties.get(name);
        try {
            return (int) value;
        } catch (ClassCastException ex) {
            throw new InvalidCastException(value, IntType.SINGLETON);
        }
    }

    public boolean isBigInt(String name) {
        final Object value = properties.get(name);
        return value instanceof Long;
    }

    public long getBigIntSafe(String name) throws InvalidCastException {
        final Object value = properties.get(name);
        try {
            return (long) value;
        } catch (ClassCastException ex) {
            throw new InvalidCastException(value, BigIntType.SINGLETON);
        }
    }

    public boolean isSmallFloat(String name) {
        final Object value = properties.get(name);
        return value instanceof Float;
    }

    public float getSmallFloatSafe(String name) throws InvalidCastException {
        final Object value = properties.get(name);
        try {
            return (float) value;
        } catch (ClassCastException ex) {
            throw new InvalidCastException(value, SmallFloatType.SINGLETON);
        }
    }

    public boolean isFloat(String name) {
        final Object value = properties.get(name);
        return value instanceof Double;
    }

    public double getFloatSafe(String name) throws InvalidCastException {
        final Object value = properties.get(name);
        try {
            return (double) value;
        } catch (ClassCastException ex) {
            throw new InvalidCastException(value, FloatType.SINGLETON);
        }
    }

    public Object get(String name) {
        return properties.get(name);
    }

    public void put(String name, Object value) {
        properties.put(name, value);
    }

    public Object createDeepCopy() {
        return new Record(this);
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<Context>> getLanguage() {
        return I4GLLanguage.class;
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return properties.toString();
    }

    public static boolean isInstance(TruffleObject obj) {
        return obj instanceof Record;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return recordType;
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

    @TruffleBoundary
    private static UnsupportedMessageException unsupported() {
        return UnsupportedMessageException.create();
    }

    @ExportMessage
    @TruffleBoundary
    void removeMember(String name) throws UnsupportedMessageException {
        throw unsupported();
    }

    @ExportMessage
    @TruffleBoundary
    Object getMembers(boolean includeInternal) {
        return new RecordNamesObject(properties.keySet().toArray());
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
