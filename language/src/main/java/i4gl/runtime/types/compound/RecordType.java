package i4gl.runtime.types.compound;

import java.util.LinkedHashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.exceptions.I4GLRuntimeException;
import i4gl.runtime.types.BaseType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.Record;

/**
 * Type descriptor for I4GL's records types. It contains additional information about the variables it contains.
 */
public class RecordType extends BaseType {

    public static final RecordType SQLCA = SqlcaRecordType();

    private final Map<String, BaseType> variables;

    private static RecordType SqlcaRecordType() {
        Map<String, BaseType> variables = new LinkedHashMap<>();
        variables.put("sqlcode", IntType.SINGLETON);
        variables.put("sqlerrm", new CharType(72));
        variables.put("sqlerrp", new CharType(8));
        variables.put("sqlerrd", new ArrayType(6, IntType.SINGLETON));
        variables.put("sqlawarn", new CharType(8));
        return new RecordType(variables);
    }

    /**
     * The default descriptor.
     * @param innerScope lexical scope containing the identifiers of the variables this record contains
     */
    public RecordType(final Map<String, BaseType> variables) {
        this.variables = variables;
    }

    public Map<String, BaseType> getVariables() {
        return variables;
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
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, BaseType> entry : variables.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getDefaultValue());
        }
        return new Record(this, values);
    }

    public boolean containsIdentifier(String identifier) {
        return variables.containsKey(identifier);
    }

    public BaseType getVariableType(String identifier) {
        return variables.get(identifier);
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
        for (Map.Entry<String, BaseType> entry : variables.entrySet()) {
            if (i++!=0) {
                builder.append(", ");
            }
            builder.append(entry.getKey());
            builder.append(" ");
            builder.append(entry.getValue().toString());
        }
        builder.append(" END RECORD");
        return builder.toString();
    }

    @Override
    public String getNullString() {
        throw new I4GLRuntimeException("Should not be here");
    }
}
