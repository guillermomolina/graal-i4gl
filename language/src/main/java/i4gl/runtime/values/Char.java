package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.compound.CharType;

/**
 * Representation of variables of NChar type. It is a slight wrapper to Java's
 * {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class Char implements TruffleObject {

    private final CharType charType;
    private String data;

    private Char(Char source) {
        this.charType = source.charType;
        this.data = source.data;
    }

    public Char(final CharType charType) {
        this(charType, " ".repeat(charType.getSize()));
    }

    public Char(final CharType charType, String value) {
        this.charType = charType;
        this.data = value;
    }

    public Char(final CharType charType, Character value) {
        this(charType, value.toString());
    }

    public int getSize() {
        return charType.getSize();
    }

    public int getLength() {
        int i = charType.getSize() - 1;
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
            data = value + " ".repeat(charType.getSize() - value.length());
        }
    }

    public void assignSmallInt(short value) {
        assignString(String.valueOf(value));
    }

    public void assignInt(int value) {
        assignString(String.valueOf(value));
    }

    public void assignBigInt(long value) {
        assignString(String.valueOf(value));
    }

    public void assignSmallFloat(float value) {
        String output = String.format("%.4g", value);
        if(!output.contains("e")) {
            output = String.valueOf(value);
        }
        assignString(output);
    }

    public void assignFloat(double value) {
        String output = String.format("%.4g", value);
        if(!output.contains("e")) {
            output = String.valueOf(value);
        }
        assignString(output);
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
        return new Char(charType, clipped);
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
    Class<? extends TruffleLanguage<Context>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return charType;
    }

    @ExportMessage
    boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    final boolean isArrayElementInsertable(long index) {
        return false;
    }

    @ExportMessage(name = "isArrayElementReadable")
    @ExportMessage(name = "isArrayElementModifiable")
    boolean inBounds(long index) {
        return 0 <= index && index < getSize();
    }

    @ExportMessage
    long getArraySize() {
        return getSize();
    }

    @ExportMessage
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
        try {
            return getCharAt((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }

    @ExportMessage
    public void writeArrayElement(long index, Object value) throws InvalidArrayIndexException {
        try {
            if (value instanceof Character) {
                Character character = (Character)value;
                setCharAt((int) index, character.charValue());                
            } else {
                throw new NotImplementedException();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }

}
