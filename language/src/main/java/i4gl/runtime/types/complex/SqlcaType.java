package i4gl.runtime.types.complex;

import java.util.LinkedHashSet;

import i4gl.runtime.types.compound.ArrayType;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.RecordField;
import i4gl.runtime.types.compound.RecordType;
import i4gl.runtime.types.primitive.IntType;
import i4gl.runtime.values.Sqlca;

public class SqlcaType extends RecordType {

    public static final SqlcaType SINGLETON = createSingleton();

    private static SqlcaType createSingleton() {
        LinkedHashSet<RecordField> fields = new LinkedHashSet<>();

        fields.add(new RecordField("sqlcode", IntType.SINGLETON));
        fields.add(new RecordField("sqlerrm", new CharType(72)));
        fields.add(new RecordField("sqlerrd", new ArrayType(6, IntType.SINGLETON)));
        fields.add(new RecordField("sqlerrp", new CharType(8)));
        fields.add(new RecordField("sqlawarn", new CharType(8)));

        return new SqlcaType(fields);
    }

    protected SqlcaType(final LinkedHashSet<RecordField> fields) {
        super(fields, Sqlca.class);
    }
}
