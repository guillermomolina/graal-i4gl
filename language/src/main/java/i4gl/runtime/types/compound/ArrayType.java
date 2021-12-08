package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import i4gl.runtime.values.Array;
import i4gl.runtime.values.BigIntArray;
import i4gl.runtime.values.CharArray;
import i4gl.runtime.values.FloatArray;
import i4gl.runtime.values.IntArray;
import i4gl.runtime.values.RecordArray;
import i4gl.runtime.values.SmallFloatArray;
import i4gl.runtime.values.SmallIntArray;

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
        return value instanceof Array;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    public Object getDefaultValue() {
        if (valuesType == Char1Type.SINGLETON) {
            return new CharArray(size);
        } else if (valuesType == SmallIntType.SINGLETON) {
            return new SmallIntArray(size);
        } else if (valuesType == IntType.SINGLETON) {
            return new IntArray(size);
        } else if (valuesType == BigIntType.SINGLETON) {
            return new BigIntArray(size);
        } else if (valuesType == SmallFloatType.SINGLETON) {
            return new SmallFloatArray(size);
        } else if (valuesType == FloatType.SINGLETON) {
            return new FloatArray(size);
        } else if (valuesType instanceof RecordType) {
            return new RecordArray((RecordType) valuesType, size);
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
