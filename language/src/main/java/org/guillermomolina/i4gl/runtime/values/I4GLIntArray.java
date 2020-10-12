package org.guillermomolina.i4gl.runtime.values;

import java.lang.reflect.Array;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.runtime.I4GLType;

@ExportLibrary(InteropLibrary.class)
public class I4GLIntArray extends I4GLArrayValue {
    private final int[] array;

    public I4GLIntArray(int size) {
        this.array = new int[size];
    }

    protected I4GLIntArray(int[] array) {
        this.array = array;
    }

    public int getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, int value) {
        array[index] = value;
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
        return I4GLType.INT;
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
