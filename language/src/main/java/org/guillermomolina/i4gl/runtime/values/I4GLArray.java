package org.guillermomolina.i4gl.runtime.values;

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

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.I4GLContext;
import org.guillermomolina.i4gl.runtime.types.I4GLType;
import org.guillermomolina.i4gl.runtime.types.compound.I4GLArrayType;

@ExportLibrary(InteropLibrary.class)
public abstract class I4GLArray implements TruffleObject {

    public abstract int getSize();

    public abstract I4GLType getElementType();

    protected abstract Object getArray();

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
        return new I4GLArrayType(getSize(), getElementType());
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    long getArraySize() {
        return getSize();
    }

    @ExportMessage
    @TruffleBoundary
    abstract Object toDisplayString(boolean allowSideEffects);

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
            return Array.get(getArray(), (int)index);
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
