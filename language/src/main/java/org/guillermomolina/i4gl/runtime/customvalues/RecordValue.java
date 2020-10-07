package org.guillermomolina.i4gl.runtime.customvalues;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

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
    @TruffleBoundary
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        return properties.keySet().toArray();
    }    
}
