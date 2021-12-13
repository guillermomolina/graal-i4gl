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
import i4gl.runtime.types.primitive.ArrayType;

@ExportLibrary(InteropLibrary.class)
public class Array implements TruffleObject {

    private final ArrayType arrayType;
    private final Object[] array;

    public Array(final ArrayType arrayType) {
        this.arrayType = arrayType;
        this.array = new Object[arrayType.getSize()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = getElementType().getDefaultValue();
        }
    }

    public Array(final Array array) {
        this.arrayType = array.arrayType;
        this.array = Arrays.copyOf(array.array, array.getSize());
    }

    protected Array(ArrayType arrayType, Object[] array) {
        this.arrayType = arrayType;
        this.array = array;
    }

    public BaseType getElementType() {
        return arrayType.getElementsType();
    }


    public Object createDeepCopy() {
        return new Array(this);
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
        return new ArrayType(getSize(), getElementType());
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
        return 0 <= index && index < array.length;
    }

    @ExportMessage
    long getArraySize() {
        return getSize();
    }

    public int getSize() {
        return array.length;
    }

    public void fill(Object value) {
        Arrays.fill(array, value);
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
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
        return array[(int) index];
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
        array[index] = value;
    }

}
