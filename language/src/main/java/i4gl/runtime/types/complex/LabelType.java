package i4gl.runtime.types.complex;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.exceptions.I4GLRuntimeException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Label;

/**
 * Type descriptor for I4GL's labels. Contains additional information about the identifier of the label.
 */
public class LabelType extends BaseType {

    private final String identifier;

    public LabelType(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Label;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "LABEL " + identifier;
    }

    @Override
    public String getNullString() {
        throw new I4GLRuntimeException("Should not be here");
    }
}
