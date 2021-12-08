package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.SmallIntType;

@ExportLibrary(InteropLibrary.class)
public class SmallIntArray extends Array {
    private final short[] array;

    public SmallIntArray(int size) {
        this.array = new short[size];
    }

    protected SmallIntArray(short[] array) {
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
    public BaseType getElementType() {
        return SmallIntType.SINGLETON;
    }
}
