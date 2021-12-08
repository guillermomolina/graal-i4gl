package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.I4GLArray;
import i4gl.runtime.values.I4GLBigIntArray;
import i4gl.runtime.values.I4GLCharArray;
import i4gl.runtime.values.I4GLFloatArray;
import i4gl.runtime.values.I4GLIntArray;
import i4gl.runtime.values.I4GLRecordArray;
import i4gl.runtime.values.I4GLSmallFloatArray;
import i4gl.runtime.values.I4GLSmallIntArray;

/**
 * Type descriptor for array values. Note that it can be only one dimensional
 * and so multidimensional arrays has to be
 * stored in a chain of these descriptors. It contains additional information
 * about the type of the inner values and
 * the universe of the indices stored inside an ordinal descriptor.
 */
public class ArrayType extends BaseType {

    protected final int size;
    private final BaseType valuesType;

    /**
     * Default constructor.
     * 
     * @param dimension  universe of the indices
     * @param valuesType type descriptor of the inner values
     */
    public ArrayType(int size, BaseType valuesType) {
        this.size = size;
        this.valuesType = valuesType;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLArray;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    public Object getDefaultValue() {
        if (valuesType == Char1Type.SINGLETON) {
            return new I4GLCharArray(size);
        } else if (valuesType == SmallIntType.SINGLETON) {
            return new I4GLSmallIntArray(size);
        } else if (valuesType == IntType.SINGLETON) {
            return new I4GLIntArray(size);
        } else if (valuesType == BigIntType.SINGLETON) {
            return new I4GLBigIntArray(size);
        } else if (valuesType == SmallFloatType.SINGLETON) {
            return new I4GLSmallFloatArray(size);
        } else if (valuesType == FloatType.SINGLETON) {
            return new I4GLFloatArray(size);
        } else if (valuesType instanceof RecordType) {
            return new I4GLRecordArray((RecordType) valuesType, size);
        } else {
            throw new NotImplementedException();
            /*
             * data = new Object[size];
             * for (int i = 0; i < data.length; ++i) {
             * data[i] = valuesType.getDefaultValue();
             * }
             */
        }
    }

    public int getSize() {
        return size;
    }

    public BaseType getValuesType() {
        return this.valuesType;
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "ARRAY[" + size + "] OF " + valuesType;
    }

}
