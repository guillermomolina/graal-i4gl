package com.guillermomolina.i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import com.guillermomolina.i4gl.exceptions.NotImplementedException;
import com.guillermomolina.i4gl.runtime.types.I4GLType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLBigIntType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLFloatType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLIntType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallFloatType;
import com.guillermomolina.i4gl.runtime.types.primitive.I4GLSmallIntType;
import com.guillermomolina.i4gl.runtime.values.I4GLArray;
import com.guillermomolina.i4gl.runtime.values.I4GLBigIntArray;
import com.guillermomolina.i4gl.runtime.values.I4GLFloatArray;
import com.guillermomolina.i4gl.runtime.values.I4GLIntArray;
import com.guillermomolina.i4gl.runtime.values.I4GLSmallFloatArray;
import com.guillermomolina.i4gl.runtime.values.I4GLSmallIntArray;

/**
 * Type descriptor for array values. Note that it can be only one dimensional and so multidimensional arrays has to be
 * stored in a chain of these descriptors. It contains additional information about the type of the inner values and
 * the universe of the indices stored inside an ordinal descriptor.
 */
public class I4GLArrayType extends I4GLType {

    protected final int size;
    private final I4GLType valuesType;

    /**
     * Default constructor.
     * @param dimension universe of the indices
     * @param valuesType type descriptor of the inner values
     */
    public I4GLArrayType(int size, I4GLType valuesType) {
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
        if (valuesType == I4GLSmallIntType.SINGLETON) {
            return new I4GLSmallIntArray(size);
        } else if (valuesType == I4GLIntType.SINGLETON) {
            return new I4GLIntArray(size);
        } else if (valuesType == I4GLBigIntType.SINGLETON) {
            return new I4GLBigIntArray(size);
        } else if (valuesType == I4GLSmallFloatType.SINGLETON) {
            return new I4GLSmallFloatArray(size);
        } else if (valuesType == I4GLFloatType.SINGLETON) {
            return new I4GLFloatArray(size);
        } else {
            throw new NotImplementedException();
            /*data = new Object[size];
            for (int i = 0; i < data.length; ++i) {
                data[i] = valuesType.getDefaultValue();
            }*/
        }
    }

    public int getSize() {
        return size;
    }

    public I4GLType getValuesType() {
        return this.valuesType;
    }

    @Override
    public boolean convertibleTo(I4GLType type) {
        return false;
    }

    @Override
    public String toString() {
        return "ARRAY[" + size + "] OF " + valuesType;
    }

}
