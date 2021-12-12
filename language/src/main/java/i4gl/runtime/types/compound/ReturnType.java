package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.exceptions.I4GLRuntimeException;
import i4gl.exceptions.ShouldNotReachHereException;
import i4gl.runtime.types.BaseType;

/**
 * Type descriptor for I4GL's returns types. It contains additional information about the variables it contains.
 */
public class ReturnType extends BaseType {
    private final BaseType[] valueTypes;

    /**
     * The default descriptor.
     * @param innerScope lexical scope containing the identifiers of the variables this return contains
     */
    public ReturnType(BaseType[] valueTypes) {
        this.valueTypes = valueTypes;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value.getClass().isArray();
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        throw new I4GLRuntimeException("Can not be here");
    }

    public int getSize() {
        return valueTypes.length;
    }

    public boolean isVoid() {
        return valueTypes.length == 0;
    }

    public BaseType getValueDescriptor(final int index) {
        return this.valueTypes[index];
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i < valueTypes.length; i++) {
            if (i!=0) {
                builder.append(", ");
            }
            builder.append(valueTypes[i].toString());
        }
        return builder.toString();
    }

    @Override
    public String getNullString() {
        throw new ShouldNotReachHereException();
    }
}
