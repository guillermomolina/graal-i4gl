package i4gl.runtime.types.compound;

import java.util.LinkedHashSet;
import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.staticobject.StaticShape;

import i4gl.exceptions.ShouldNotReachHereException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.values.Record;

public class RecordType extends BaseType {
    private final LinkedHashSet<RecordField> fields;
    private final StaticShape<Factory> shape;

    public RecordType(final LinkedHashSet<RecordField> fields) {
        this(fields, Record.class);
    }

    public RecordType(final LinkedHashSet<RecordField> fields, Class<?> clazz) {
        this.fields = fields;
        StaticShape.Builder builder = StaticShape.newBuilder(getI4GLLanguage());
        for (RecordField field : fields) {
            field.addToBuilder(builder);
        }
        this.shape = builder.build(clazz, Factory.class);
    }

    public static RecordType valueOf(final Map<String, BaseType> variables) {
        LinkedHashSet<RecordField> fields = new LinkedHashSet<>();
        for (var variableEntry : variables.entrySet()) {
            fields.add(new RecordField(variableEntry.getKey(), variableEntry.getValue()));
        }
        return new RecordType(fields);
    }

    public LinkedHashSet<RecordField> getFields() {
        return fields;
    }

    public RecordField getField(String identifier) {
        return fields.stream().filter(field -> identifier.equals(field.getId())).findFirst().orElse(null);
    }

    public boolean containsIdentifier(String identifier) {
        return getField(identifier) != null;
    }

    public BaseType getFieldType(String identifier) {
        RecordField field = getField(identifier);
        if (field != null) {
            return field.getType();
        }
        return null;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof Record;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        var defaultValue = shape.getFactory().create(this);
        for (RecordField field : fields) {
            final BaseType fieldType = field.getType();
            defaultValue.setObject(field.getId(), fieldType.getDefaultValue());
        }
        return defaultValue;
    }

    @Override
    public boolean convertibleTo(BaseType type) {
        return false;
    }

    @Override
    public String toString() {
        return "RECORD";
    }

    public String toString2() {
        StringBuilder builder = new StringBuilder();
        builder.append("RECORD ");
        int i = 0;
        for (RecordField field : fields) {
            if (i++ != 0) {
                builder.append(", ");
            }
            builder.append(field.getId());
            builder.append(" ");
            builder.append(field.getType().toString());
        }
        builder.append(" END RECORD");
        return builder.toString();
    }

    @Override
    public String getNullString() {
        throw new ShouldNotReachHereException();
    }

    public interface Factory {
        Record create(final RecordType recordType);
    }

}
