package i4gl.runtime.types.compound;

import com.oracle.truffle.api.staticobject.StaticProperty;
import com.oracle.truffle.api.staticobject.StaticShape;

import i4gl.runtime.types.BaseType;

public class RecordField extends StaticProperty {
    final String identifier;
    final BaseType type;

    public RecordField(String identifier, BaseType type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String getId() {
        return identifier;
    }

    public BaseType getType() {
        return type;
    }

    public StaticShape.Builder addToBuilder(final StaticShape.Builder builder) {
        return builder.property(this, Object.class /* type.getPrimitiveClass() */, false);
    }
}
