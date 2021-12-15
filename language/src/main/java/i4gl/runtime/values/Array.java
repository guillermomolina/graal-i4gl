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
import i4gl.runtime.context.Context;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.ArrayType;

@ExportLibrary(InteropLibrary.class)
public class Array implements TruffleObject {

    private final ArrayType arrayType;

    public Array(final ArrayType arrayType) {
        this.arrayType = arrayType;
    }

    public BaseType getElementType() {
        return arrayType.getElementsType();
    }

    public Object[] getArray() {
        return (Object[]) arrayType.getElement().getObject(this);
    }

    public void setArray(Object[] value) {
        arrayType.getElement().setObject(this, value);
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
        return arrayType;
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

    public int getSize() {
        return arrayType.getSize();
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(getArray());
    }

    @ExportMessage
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
        try {
            return getValueAt((int) index);
        } catch (ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }

    public Object getValueAt(int index) throws InvalidArrayIndexException {
        if (!inBounds(index)) {
            throw InvalidArrayIndexException.create(index);
        }
        return getArray()[(int) index];
    }

    @ExportMessage
    public void writeArrayElement(long index, Object value) throws InvalidArrayIndexException {
        try {
            setValueAt((int) index, value);
        } catch (ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }

    public void setValueAt(int index, Object value) throws InvalidArrayIndexException {
        if (!inBounds(index)) {
            throw InvalidArrayIndexException.create(index);
        }
        getArray()[index] = value;
    }

}
