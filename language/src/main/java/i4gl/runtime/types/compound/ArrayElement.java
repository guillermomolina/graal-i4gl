package i4gl.runtime.types.compound;

import com.oracle.truffle.api.staticobject.StaticProperty;
import com.oracle.truffle.api.staticobject.StaticShape;

import i4gl.runtime.types.BaseType;

public class ArrayElement extends StaticProperty {
    final BaseType type;

    public ArrayElement(BaseType type) {
        this.type = type;
    }

    @Override
    public String getId() {
        return "element";
    }

    public BaseType getType() {
        return type;
    }

    public StaticShape.Builder addToBuilder(final StaticShape.Builder builder) {
        return builder.property(this, Object.class, false);
    }
}
