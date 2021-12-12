package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.ShouldNotReachHereException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.BigIntType;
import i4gl.runtime.types.primitive.FloatType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.types.primitive.SmallFloatType;
import i4gl.runtime.types.primitive.SmallIntType;
import i4gl.runtime.values.Array;

/**
 * Type descriptor for array values. Note that it can be only one dimensional
 * and so multidimensional arrays has to be
 * stored in a chain of these descriptors. It contains additional information
 * about the type of the inner values and
 * the universe of the indices stored inside an ordinal descriptor.
 */
public class ArrayType extends BaseType {

    protected final int size;
    private final BaseType elementsType;

    /**
     * Default constructor.
     * 
     * @param dimension    universe of the indices
     * @param elementsType type descriptor of the inner values
     */
    public ArrayType(int size, BaseType elementsType) {
        this.size = size;
        this.elementsType = elementsType;
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
        if (elementsType == Char1Type.SINGLETON) {
            return new char[size];
        } else if (elementsType == SmallIntType.SINGLETON) {
            return new short[size];
        } else if (elementsType == IntType.SINGLETON) {
            return new int[size];
        } else if (elementsType == BigIntType.SINGLETON) {
            return new long[size];
        } else if (elementsType == SmallFloatType.SINGLETON) {
            return new float[size];
        } else if (elementsType == FloatType.SINGLETON) {
            return new double[size];
        } else {
            return new Array(this);
        }        
    }

    public int getSize() {
        return size;
    }

    public BaseType getElementsType() {
        return this.elementsType;
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "ARRAY[" + size + "] OF " + elementsType;
    }

    @Override
    public String getNullString() {
        throw new ShouldNotReachHereException();
    }
}
