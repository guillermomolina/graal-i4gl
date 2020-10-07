package org.guillermomolina.i4gl.runtime.customvalues;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.runtime.I4GLType;
import org.guillermomolina.i4gl.runtime.exceptions.IndexOutOfBoundsException;

/**
 * Representation of variables of Varchar type. It is a slight wrapper to Java's {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class VarcharValue implements TruffleObject {

    private String data;
    private final int size;

    public VarcharValue(int size) {
        this.size = size;
        this.data = "";
    }

    private VarcharValue(VarcharValue source) {
        this.size = source.size;
        this.data = source.data;
    }

    public VarcharValue(String value) {
        this.size = value.length();
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
        return new VarcharValue(this);
    }

    private void checkArrayIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static VarcharValue concat(VarcharValue left, VarcharValue right) {
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
        return I4GLType.VARCHAR;
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
