package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.types.compound.Char1Type;
import i4gl.runtime.types.compound.CharType;

/**
 * Representation of variables of NChar type. It is a slight wrapper to Java's
 * {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class Char implements TruffleObject {
    private String data;

    public Char(int size) {
        char[] chars = new char[size];
        char value = 0;
        Arrays.fill(chars, value);
        this.data = new String(chars);
    }

    private Char(Char source) {
        this.data = source.data;
    }

    public Char(String value) {
        this.data = value;
    }

    public Char(Character value) {
        this.data = value.toString();
    }

    public int getSize() {
        return data.length();
    }

    public int getLength() {
        int i = data.length() - 1;
        while (i >= 0 && Character.isWhitespace(data.charAt(i))) {
            i--;
        }
        return i + 1;
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
        data = data.substring(0, index) + value + data.substring(index + 1);
    }

    public void fill(char value) {
        char[] chars = new char[getSize()];
        Arrays.fill(chars, value);
        this.data = new String(chars);
    }

    public Object createDeepCopy() {
        return new Char(this);
    }

    public Object clipped() {
        String clipped = data.substring(0, getLength());
        return new Char(clipped);
    }

    private void checkArrayIndex(int index) {
        if (index >= getSize()) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static Char concat(Char left, Char right) {
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
        if (getSize() == 1) {
            return Char1Type.SINGLETON;
        }
        return new CharType(getSize());
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
