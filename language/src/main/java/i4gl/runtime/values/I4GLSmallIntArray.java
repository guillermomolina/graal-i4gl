package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.primitive.I4GLSmallIntType;

@ExportLibrary(InteropLibrary.class)
public class I4GLSmallIntArray extends I4GLArray {
    private final short[] array;

    public I4GLSmallIntArray(int size) {
        this.array = new short[size];
    }

    protected I4GLSmallIntArray(short[] array) {
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    public short getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, short value) {
        array[index] = value;
    }

    public void fill(short value) {
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
        return I4GLSmallIntType.SINGLETON;
    }
}
