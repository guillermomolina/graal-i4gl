package i4gl.runtime.types.compound;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.staticobject.StaticShape;

import i4gl.exceptions.ShouldNotReachHereException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Array;

public class ArrayType extends BaseType {

    protected final int size;
    private final ArrayElement element;
    private final StaticShape<Factory> shape;

    public ArrayType(final int size, final BaseType elementsType) {
        this.size = size;
        this.element = new ArrayElement(elementsType);
        StaticShape.Builder builder = StaticShape.newBuilder(getI4GLLanguage());
        element.addToBuilder(builder);
        this.shape = builder.build(Array.class, Factory.class);
    }

    public int getSize() {
        return size;
    }

    public ArrayElement getElement() {
        return element;
    }

    public BaseType getElementsType() {
        return element.getType();
    }

    @Override
    public Object getDefaultValue() {
        Array defaultValue = shape.getFactory().create(this);
        final BaseType elementType = element.getType();
        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = elementType.getDefaultValue();
        }
        defaultValue.setArray(array);
        return defaultValue;
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

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "ARRAY[" + size + "] OF " + element.getType();
    }

    @Override
    public String getNullString() {
        throw new ShouldNotReachHereException();
    }

    public interface Factory {
        Array create(final ArrayType arrayType);
    }
}
