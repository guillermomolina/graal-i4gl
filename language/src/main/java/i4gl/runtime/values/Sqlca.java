package i4gl.runtime.values;

import i4gl.runtime.types.compound.RecordType;

public abstract class Sqlca extends Record {
    public int sqlcode;
    public Char sqlerrm;
    public Char sqlerrp;
    public int[] sqlerrd;
    public Char sqlawarn;

    public Sqlca(final RecordType recordType) {
        super(recordType);
        this.sqlcode = 0;
        this.sqlerrm = new Char(72);
        this.sqlerrp = new Char(8);        
        this.sqlerrd = new int[]{0, 0, 0, 0, 0, 0};
        this.sqlawarn = new Char(8);
    }
}
