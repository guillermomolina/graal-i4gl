package i4gl.runtime.values;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.compound.RecordType;

@ExportLibrary(InteropLibrary.class)
public class RecordArray extends Array {

    private final RecordType recordType;
    private final Record[] array;

    public RecordArray(final RecordType recordType, int size) {
        this.recordType = recordType;
        this.array = new Record[size];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (Record)recordType.getDefaultValue();
        }
    }

    protected RecordArray(final RecordType recordType, Record[] array) {
        this.recordType = recordType;
        this.array = array;
    }

    @Override
    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return Arrays.toString(array);
    }

    public Record getValueAt(int index) {
        return array[index];
    }

    public void setValueAt(int index, Record value) {
        array[index] = value;
    }

    public void fill(Record value) {
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
        return recordType;
    }
}
