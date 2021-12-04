package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.compound.I4GLRecordType;

@ExportLibrary(InteropLibrary.class)
public class I4GLRecordArray extends I4GLArray {

    private final I4GLRecordType recordType;
    private final I4GLRecord[] array;

    public I4GLRecordArray(final I4GLRecordType recordType, int size) {
        this.recordType = recordType;
        this.array = new I4GLRecord[size];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (I4GLRecord)recordType.getDefaultValue();
        }
    }

    protected I4GLRecordArray(final I4GLRecordType recordType, I4GLRecord[] array) {
        this.recordType = recordType;
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    public I4GLRecord getValueAt(int index) {
        return array[index];
    }

    public void setValueAt(int index, I4GLRecord value) {
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
        return recordType;
    }
}
