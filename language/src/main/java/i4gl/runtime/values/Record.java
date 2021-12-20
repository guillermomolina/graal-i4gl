package i4gl.runtime.values;

import java.util.stream.Collectors;

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
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.compound.RecordField;
import i4gl.runtime.types.compound.RecordType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;

@ExportLibrary(InteropLibrary.class)
public class Record implements TruffleObject {

    protected final RecordType recordType;

    public Record(final RecordType recordType) {
        this.recordType = recordType;
    }

    public Record(Record record) {
        this.recordType = record.recordType;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public boolean isSmallInt(String identifier) {
        return recordType.getFieldType(identifier) == SmallIntType.SINGLETON;
    }

    public short getSmallInt(String identifier) {
        return recordType.getField(identifier).getShort(this);
    }
    
    public void setSmallInt(String identifier, short value) {
        recordType.getField(identifier).setShort(this, value);
    }

    public boolean isInt(String identifier) {
        return recordType.getFieldType(identifier) == IntType.SINGLETON;
    }

    public int getInt(String identifier) {
        return recordType.getField(identifier).getInt(this);
    }
    
    public void setInt(String identifier, int value) {
        recordType.getField(identifier).setInt(this, value);
    }

    public boolean isBigInt(String identifier) {
        return recordType.getFieldType(identifier) == BigIntType.SINGLETON;
    }

    public long getBigInt(String identifier) {
        return recordType.getField(identifier).getLong(this);
    }
    
    public void setBigInt(String identifier, long value) {
        recordType.getField(identifier).setLong(this, value);
    }

    public boolean isSmallFloat(String identifier) {
        return recordType.getFieldType(identifier) == SmallFloatType.SINGLETON;
    }

    public float getSmallFloat(String identifier) {
        return recordType.getField(identifier).getFloat(this);
    }
    
    public void setSmallFloat(String identifier, float value) {
        recordType.getField(identifier).setFloat(this, value);
    }

    public boolean isFloat(String identifier) {
        return recordType.getFieldType(identifier) == FloatType.SINGLETON;
    }

    public double getFloat(String identifier) {
        return recordType.getField(identifier).getDouble(this);
    }
    
    public void setFloat(String identifier, double value) {
        recordType.getField(identifier).setDouble(this, value);
    }

    public Object getObject(String identifier) {
        return recordType.getField(identifier).getObject(this);
    }

    public void setObject(String identifier, Object value) {
        recordType.getField(identifier).setObject(this, value);
    }

    public void set(String identifier, short value) {        
        setSmallInt(identifier, value);
    }

    public void set(String identifier, int value) {        
        setInt(identifier, value);
    }

    public void set(String identifier, long value) {        
        setBigInt(identifier, value);
    }

    public void set(String identifier, float value) {        
        setSmallFloat(identifier, value);
    }

    public void set(String identifier, double value) {        
        setFloat(identifier, value);
    }
    
    public void set(String identifier, Object value) {        
        setObject(identifier, value);
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
        throw new NotImplementedException();
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        throw new NotImplementedException();
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
        return recordType.containsIdentifier(name);
    }

    @ExportMessage
    boolean isMemberInsertable(String name) {
        return !hasMember(name);
    }

    @ExportMessage
    @TruffleBoundary
    Object readMember(String name) throws UnknownIdentifierException {
        Object value = recordType.getField(name).getObject(this);
        if (value == null) {
            throw UnknownIdentifierException.create(name);
        }
        return value;
    }

    @ExportMessage
    @TruffleBoundary
    void writeMember(String name, Object value) {
        recordType.getField(name).setObject(this, value);
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
        Object[] names = recordType.getFields().stream().map(RecordField::getId).collect(Collectors.toList()).toArray();
        return new RecordNamesObject(names);
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
