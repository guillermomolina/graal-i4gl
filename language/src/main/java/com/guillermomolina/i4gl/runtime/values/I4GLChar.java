package com.guillermomolina.i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import com.guillermomolina.i4gl.I4GLLanguage;
import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLChar1Type;
import com.guillermomolina.i4gl.runtime.types.compound.I4GLCharType;

/**
 * Representation of variables of NChar type. It is a slight wrapper to Java's
 * {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class I4GLChar implements TruffleObject {
    private String data;

    public I4GLChar(int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, ' ');
        this.data = new String(chars);
    }

    private I4GLChar(I4GLChar source) {
        this.data = source.data;
    }

    public I4GLChar(String value) {
        this.data = value;
    }

    public I4GLChar(Character value) {
        this.data = value.toString();
    }

    public int getSize() {
        return data.length();
    }

    public void assignString(String value) {
        final int size = getSize();
        if (value.length() > size) {
            data = value.substring(0, size);
        } else {
            final StringBuilder str = new StringBuilder(value);
            for (int i = value.length(); i < size; ++i) {
                str.append(' ');
            }
            data = str.toString();
        }
    }

    public char getCharAt(int index) {
        return data.charAt(index);
    }

    public void setCharAt(int index, char value) {
        checkArrayIndex(index);
        data = this.data.substring(0, index) + value + this.data.substring(index + 1);
    }

    public Object createDeepCopy() {
        return new I4GLChar(this);
    }

    public Object clipped() {
        int i = data.length()-1;
        while (i >= 0 && Character.isWhitespace(data.charAt(i))) {
            i--;
        }
        String clipped = data.substring(0,i+1);
        return new I4GLChar(clipped);
    }

    private void checkArrayIndex(int index) {
        if (index >= getSize()) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static I4GLChar concat(I4GLChar left, I4GLChar right) {
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
        if(getSize() == 1) {
            return I4GLChar1Type.SINGLETON;
        }
        return new I4GLCharType(getSize());
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
