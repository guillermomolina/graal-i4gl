package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.DoubleDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;
import org.guillermomolina.i4gl.runtime.customvalues.ArrayValue;

/**
 * Type descriptor for array values. Note that it can be only one dimensional and so multidimensional arrays has to be
 * stored in a chain of these descriptors. It contains additional information about the type of the inner values and
 * the universe of the indices stored inside an ordinal descriptor.
 */
public class ArrayDescriptor implements I4GLTypeDescriptor {

    protected final int size;
    private final I4GLTypeDescriptor valuesDescriptor;

    /**
     * Default constructor.
     * @param dimension universe of the indices
     * @param valuesDescriptor type descriptor of the inner values
     */
    public ArrayDescriptor(int size, I4GLTypeDescriptor valuesDescriptor) {
        this.size = size;
        this.valuesDescriptor = valuesDescriptor;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    public Object getDefaultValue() {
        Object[] data;
        if (valuesDescriptor == IntDescriptor.SINGLETON) {
            data = getDefaultIntValue();
        } else if (valuesDescriptor == BigIntDescriptor.SINGLETON) {
            data = getDefaultBigIntValue();
        } else if (valuesDescriptor == SmallFloatDescriptor.SINGLETON) {
            data = getDefaultSmallFloatValue();
        } else if (valuesDescriptor == DoubleDescriptor.SINGLETON) {
            data = getDefaultDoubleValue();
        } else {
            data = new Object[size];
            for (int i = 0; i < data.length; ++i) {
                data[i] = valuesDescriptor.getDefaultValue();
            }
        }
        return new ArrayValue(data);
    }

    private Object[] getDefaultIntValue() {
        Integer[] data = new Integer[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = 0;
        }
        return data;
    }

    private Object[] getDefaultBigIntValue() {
        Long[] data = new Long[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = 0l;
        }
        return data;
    }

    private Object[] getDefaultSmallFloatValue() {
        Float[] data = new Float[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = 0f;
        }
        return data;
    }

    private Object[] getDefaultDoubleValue() {
        Double[] data = new Double[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = 0d;
        }
        return data;
    }

    public int getSize() {
        return size;
    }

    public I4GLTypeDescriptor getValuesDescriptor() {
        return this.valuesDescriptor;
    }

    @Override
    public boolean convertibleTo(I4GLTypeDescriptor type) {
        return false;
    }

    @Override
    public String toString() {
        return "ARRAY[" + size + "] OF " + valuesDescriptor;
    }

}
