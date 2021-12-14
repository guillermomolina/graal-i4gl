package i4gl.runtime.values;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;

import i4gl.I4GLLanguage;
import i4gl.runtime.types.compound.RecordType;

public abstract class Sqlca extends Record {
    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(Sqlca.class);

    /*
     * protected int sqlcode;
     * protected Char sqlerrm;
     * protected Char sqlerrp;
     * protected int[] sqlerrd;
     * protected Char sqlawarn;
     */

    public Sqlca(final RecordType recordType) {
        super(recordType);
        /*
         * this.sqlcode = 0;
         * this.sqlerrm = new Char(72);
         * this.sqlerrp = new Char(8);
         * this.sqlerrd = new int[] { 0, 0, 0, 0, 0, 0 };
         * this.sqlawarn = new Char(8);
         */
    }

    public void setSqlcode(final int code) {
        setObject("sqlcode", code);
    }

    public void setSqlerrd(int index, int value) {
        Array sqlerrd = (Array) getObject("sqlerrd");
        try {
            sqlerrd.setValueAt(index, value);
        } catch (InvalidArrayIndexException e) {
            LOGGER.warning("Try to set sqlcode.sqlerrd[" + index + "]=" + value + " error: " + e.getMessage());
        }
    }

    public void setSqlerrm(String message) {
        Char sqlerrm = (Char) getObject("sqlerrm");
        sqlerrm.assignString(message);
    }

    public void setSqlawarn(int index) {
        Char sqlawarn = (Char) getObject("sqlawarn");
        sqlawarn.setCharAt(0, 'W');
        sqlawarn.setCharAt(index - 1, 'W');
    }
}
