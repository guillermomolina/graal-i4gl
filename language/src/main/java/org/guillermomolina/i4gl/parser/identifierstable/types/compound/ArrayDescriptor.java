package org.guillermomolina.i4gl.parser.identifierstable.types.compound;

import com.oracle.truffle.api.frame.FrameSlotKind;
import org.guillermomolina.i4gl.parser.identifierstable.types.complex.OrdinalDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.constant.LongConstantDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.TypeDescriptor;
import org.guillermomolina.i4gl.parser.identifierstable.types.primitive.*;

/**
 * Type descriptor for array values. Note that it can be only one dimensional and so multidimensional arrays has to be
 * stored in a chain of these descriptors. It contains additional information about the type of the inner values and
 * the universe of the indices stored inside an ordinal descriptor.
 */
public class ArrayDescriptor implements TypeDescriptor {

    private final OrdinalDescriptor dimension;
    private final TypeDescriptor valuesDescriptor;

    /**
     * Default constructor.
     * @param dimension universe of the indices
     * @param valuesDescriptor type descriptor of the inner values
     */
    public ArrayDescriptor(int size, TypeDescriptor valuesDescriptor) {
        this.dimension = new OrdinalDescriptor.RangeDescriptor(new LongConstantDescriptor(0), new LongConstantDescriptor(size));
        this.valuesDescriptor = valuesDescriptor;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        if (valuesDescriptor == IntDescriptor.getInstance()) {
            return new int[this.dimension.getSize()];
        } else if (valuesDescriptor == LongDescriptor.getInstance()) {
            return new long[this.dimension.getSize()];
        } else if (valuesDescriptor == RealDescriptor.getInstance()) {
            return new double[this.dimension.getSize()];
        } else if (valuesDescriptor == CharDescriptor.getInstance()) {
            return new char[this.dimension.getSize()];
        } else {
            Object[] data = new Object[dimension.getSize()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = valuesDescriptor.getDefaultValue();
            }
            return data;
        }
    }

    /**
     * Gets the offset of the array indices. I4GL array does not have to begin at index 0 or any other hardwired index
     * number.
     */
    public int getOffset() {
        return this.dimension.getFirstIndex();
    }

    public int getSize() {
        return this.dimension.getSize();
    }

    public TypeDescriptor getValuesDescriptor() {
        return this.valuesDescriptor;
    }

    @Override
    public boolean convertibleTo(TypeDescriptor type) {
        return false;
    }

}
