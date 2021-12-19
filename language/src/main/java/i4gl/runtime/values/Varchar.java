package i4gl.runtime.values;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.exceptions.IndexOutOfBoundsException;
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.Context;
import i4gl.runtime.types.compound.VarcharType;

/**
 * Representation of variables of Varchar type. It is a slight wrapper to Java's
 * {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class Varchar implements TruffleObject {

    private final VarcharType varcharType;
    private String data;

    public Varchar(final VarcharType varcharType) {
        this(varcharType, "");
    }

    public Varchar(final VarcharType varcharType, String value) {
        this.varcharType = varcharType;
        this.data = value;
    }

    private Varchar(Varchar source) {
        this.varcharType = source.varcharType;
        this.data = source.data;
    }

    public void assignString(String value) {
        data = value.substring(0, Math.min(varcharType.getSize(), value.length()));
    }

    private void assignFullString(String value) {
        assignString(value);
        data = data + " ".repeat(varcharType.getSize() - data.length());
    }

    public void assignSmallInt(short value) {
        assignFullString(String.valueOf(value));
    }

    public void assignInt(int value) {
        assignFullString(String.valueOf(value));
    }

    public void assignBigInt(long value) {
        assignFullString(String.valueOf(value));
    }

    public void assignSmallFloat(float value) {
        String output = String.format("%.4g", value);
        if(!output.contains("e")) {
            output = String.valueOf(value);
        }
        assignFullString(output);
    }

    public void assignFloat(double value) {
        String output = String.format("%.4g", value);
        if(!output.contains("e")) {
            output = String.valueOf(value);
        }
        assignFullString(output);
    }

    public int getSize() {
        return data.length();
    }

    public char getCharAt(int index) {
        checkArrayIndex(index);
        return data.charAt(index);
    }

    public void setCharAt(int index, char value) {
        checkArrayIndex(index);
        if (index > data.length()) {
            final StringBuilder str = new StringBuilder(data);
            for (int i = data.length(); i < index; ++i) {
                str.append(' ');
            }
            str.append(value);
            data = str.toString();
        } else {
            data = data.substring(0, index) + value + data.substring(index + 1);
        }
    }

    public Object createDeepCopy() {
        return new Varchar(this);
    }

    private void checkArrayIndex(int index) {
        if (index < 0 || index >= varcharType.getSize()) {
            throw new IndexOutOfBoundsException(index);
        }
    }

    public static Varchar concat(Varchar left, Varchar right) {
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
        return varcharType;
    }

    @ExportMessage
    public String asString() {
        return data;
    }

    @ExportMessage
    boolean isString() {
        return true;
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
                Character character = (Character) value;
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
