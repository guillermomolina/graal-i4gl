package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.primitive.I4GLBigIntType;

@ExportLibrary(InteropLibrary.class)
public class I4GLBigIntArray extends I4GLArray {
    private final long[] array;

    public I4GLBigIntArray(int size) {
        this.array = new long[size];
    }

    protected I4GLBigIntArray(long[] array) {
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    public long getValueAt(int index) {
        return array[index];
    } 

    public void setValueAt(int index, long value) {
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
        return I4GLBigIntType.SINGLETON;
    }
}
