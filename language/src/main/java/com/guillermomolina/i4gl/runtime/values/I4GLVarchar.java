package com.guillermomolina.i4gl.runtime.values;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import com.guillermomolina.i4gl.I4GLLanguage;
import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.guillermomolina.i4gl.runtime.exceptions.IndexOutOfBoundsException;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLVarcharType;

/**
 * Representation of variables of Varchar type. It is a slight wrapper to Java's {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class I4GLVarchar implements TruffleObject {

    private String data;
    private final int size;

    public I4GLVarchar(int size) {
        this.size = size;
        this.data = "";
    }

    private I4GLVarchar(I4GLVarchar source) {
        this.size = source.size;
        this.data = source.data;
    }

    public I4GLVarchar(String value) {
        this.size = value.length();
        this.data = value;
    }

    public I4GLVarchar(int size, String value) {
        this.size = size;
        this.data = value;
    }

    public void assignString(String value) {
        data = value.substring(0, Math.min(size, value.length()));
    }

    public char getCharAt(int index) {
        checkArrayIndex(index);
        return data.charAt(index);
    }

    public void setCharAt(int index, char value) {
        checkArrayIndex(index);
        if(index > data.length()) {
            final StringBuilder str = new StringBuilder(data);
            for (int i = data.length(); i < index; ++i) {
                str.append(' ');
            }
            str.append(value);
            data = str.toString();    
        }
        else {
            data = data.substring(0, index) + value + data.substring(index + 1);
        }
    }

    public Object createDeepCopy() {
        return new I4GLVarchar(this);
    }

    private void checkArrayIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static I4GLVarchar concat(I4GLVarchar left, I4GLVarchar right) {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return data;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return '"' + data + '"';
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
        return new I4GLVarcharType(size);
    }

    @ExportMessage
    public String asString() {
        return data;
    }

    @ExportMessage
    boolean isString() {
        return true;
    }
}
