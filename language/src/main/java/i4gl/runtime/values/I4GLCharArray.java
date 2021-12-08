package i4gl.runtime.values;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLChar1Type;

@ExportLibrary(InteropLibrary.class)
public class I4GLCharArray extends I4GLArray {
    private final char[] array;

    public I4GLCharArray(int size) {
        this.array = new char[size];
    }

    protected I4GLCharArray(char[] array) {
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    public char getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, char value) {
        array[index] = value;
    } 

    public void fill(char value) {
        Arrays.fill(array, value);
    }
    
    @Override
    protected Object getArray() {
        return array;
    }

    @Override
    public int getSize() {
        return array.length;
    }

    @Override
    public I4GLType getElementType() {
        return I4GLChar1Type.SINGLETON;
    }

    @ExportMessage
    @Override
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
        try{
            return Array.get(array, (int)index);
        } catch(ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }
}
