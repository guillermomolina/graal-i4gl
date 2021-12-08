package i4gl.runtime.values;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.I4GLLanguage;
import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.context.I4GLContext;
import i4gl.runtime.exceptions.IndexOutOfBoundsException;
import i4gl.runtime.types.compound.VarcharType;

/**
 * Representation of variables of Varchar type. It is a slight wrapper to Java's {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class Varchar implements TruffleObject {

    private String data;
    private final int size;

    public Varchar(int size) {
        this.size = size;
        this.data = "";
    }

    private Varchar(Varchar source) {
        this.size = source.size;
        this.data = source.data;
    }

    public Varchar(String value) {
        this.size = value.length();
        this.data = value;
    }

    public Varchar(int size, String value) {
        this.size = size;
        this.data = value;
    }

    public void assignString(String value) {
        data = value.substring(0, Math.min(size, value.length()));
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
        return new Varchar(this);
    }

    private void checkArrayIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
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
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return new VarcharType(size);
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
