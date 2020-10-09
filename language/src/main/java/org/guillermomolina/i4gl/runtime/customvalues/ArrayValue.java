package org.guillermomolina.i4gl.runtime.customvalues;

import java.lang.reflect.Array;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class ArrayValue implements TruffleObject {
    private final Object[] data;

    public ArrayValue(Object[] data) {
        this.data = data;
    }

    public Object getValueAt(int index) {
        return data[index];
    } 

    public void setValueAt(int index, Object value) {
        data[index] = value;
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
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return I4GLType.ARRAY;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {

        return "[" + data.length + "]";
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return data.length;
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
        return index >= 0 && index < getArraySize();
    }

    @ExportMessage
    final boolean isArrayElementModifiable(long index) {
        return false;
    }

    @ExportMessage
    final boolean isArrayElementInsertable(long index) {
        return false;
    }

    @ExportMessage
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
        try{
            return Array.get(data, (int)index);
        } catch(ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }

    @TruffleBoundary
    private static UnsupportedMessageException unsupported() {
        return UnsupportedMessageException.create();
    }

    @ExportMessage
    final void writeArrayElement(long index, Object arg2) throws UnsupportedMessageException {
        throw unsupported();
    }
}
