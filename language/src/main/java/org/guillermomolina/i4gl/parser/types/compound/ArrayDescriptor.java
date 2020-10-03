package org.guillermomolina.i4gl.parser.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;

import org.guillermomolina.i4gl.parser.types.I4GLTypeDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.BigIntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.DoubleDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.IntDescriptor;
import org.guillermomolina.i4gl.parser.types.primitive.SmallFloatDescriptor;

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

    @Override
    public Object getDefaultValue() {
        if (valuesDescriptor == IntDescriptor.SINGLETON) {
            return new int[size];
        } else if (valuesDescriptor == BigIntDescriptor.SINGLETON) {
            return new long[size];
        } else if (valuesDescriptor == SmallFloatDescriptor.SINGLETON) {
            return new float[size];
        } else if (valuesDescriptor == DoubleDescriptor.SINGLETON) {
            return new double[size];
        } else {
            Object[] data = new Object[size];
            for (int i = 0; i < data.length; ++i) {
                data[i] = valuesDescriptor.getDefaultValue();
            }
            return data;
        }
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
