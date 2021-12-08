package i4gl.runtime.types.compound;

import java.util.LinkedHashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.interop.InteropLibrary;

import i4gl.runtime.types.I4GLType;
import i4gl.runtime.types.primitive.I4GLIntType;
import i4gl.runtime.values.I4GLRecord;

/**
 * Type descriptor for I4GL's records types. It contains additional information about the variables it contains.
 */
public class I4GLRecordType extends I4GLType {

    public static final I4GLRecordType SQLCA = SqlcaRecordType();

    private final Map<String, I4GLType> variables;

    private static I4GLRecordType SqlcaRecordType() {
        Map<String, I4GLType> variables = new LinkedHashMap<>();
        variables.put("sqlcode", I4GLIntType.SINGLETON);
        variables.put("sqlerrm", new I4GLCharType(72));
        variables.put("sqlerrp", new I4GLCharType(8));
        variables.put("sqlerrd", new I4GLArrayType(6, I4GLIntType.SINGLETON));
        variables.put("sqlawarn", new I4GLCharType(8));
        return new I4GLRecordType(variables);
    }

    /**
     * The default descriptor.
     * @param innerScope lexical scope containing the identifiers of the variables this record contains
     */
    public I4GLRecordType(final Map<String, I4GLType> variables) {
        this.variables = variables;
    }

    public Map<String, I4GLType> getVariables() {
        return variables;
    }

    @Override
    public boolean isInstance(Object value, InteropLibrary library) {
        CompilerAsserts.partialEvaluationConstant(this);
        return value instanceof I4GLRecord;
    }

    @Override
    public FrameSlotKind getSlotKind() {
        return FrameSlotKind.Object;
    }

    @Override
    public Object getDefaultValue() {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<String, I4GLType> entry : variables.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getDefaultValue());
        }
        return new I4GLRecord(this, values);
    }

    public boolean containsIdentifier(String identifier) {
        return variables.containsKey(identifier);
    }

    public I4GLType getVariableType(String identifier) {
        return variables.get(identifier);
    }

    @Override
    public boolean convertibleTo(I4GLType type) {
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
        for (Map.Entry<String, I4GLType> entry : variables.entrySet()) {
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
}
