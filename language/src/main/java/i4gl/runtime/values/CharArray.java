package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.Char1Type;

@ExportLibrary(InteropLibrary.class)
public class CharArray extends Array {
    private final char[] array;

    public CharArray(int size) {
        this.array = new char[size];
    }

    protected CharArray(char[] array) {
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
    public BaseType getElementType() {
        return Char1Type.SINGLETON;
    }

    @ExportMessage
    @Override
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
        try{
            return java.lang.reflect.Array.get(array, (int)index);
        } catch(ArrayIndexOutOfBoundsException e) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }
}
