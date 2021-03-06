package com.guillermomolina.i4gl.runtime.values;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;

@ExportLibrary(InteropLibrary.class)
public class I4GLIntArray extends I4GLArray {
    private final int[] array;

    public I4GLIntArray(int size) {
        this.array = new int[size];
    }

    protected I4GLIntArray(int[] array) {
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
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
        return I4GLIntType.SINGLETON;
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
