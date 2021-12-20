package i4gl.runtime.values;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;

import i4gl.I4GLLanguage;
import i4gl.runtime.types.complex.SqlcaType;
import i4gl.runtime.types.compound.RecordType;

public class Sqlca extends Record {
    private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(Sqlca.class);

    public static final Sqlca SINGLETON = (Sqlca)SqlcaType.SINGLETON.getDefaultValue();

    public Sqlca(final RecordType recordType) {
        super(recordType);
    }

    public void reset() {
        setObject("sqlcode", 0);
        ((Char) getObject("sqlerrm")).assignString("");
        for(int i = 0; i < 6; i++) {
            setSqlerrd(i, 0);
        }
        ((Char) getObject("sqlerrp")).assignString("");
        ((Char) getObject("sqlawarn")).assignString("");
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
        Object o = getObject("sqlawarn");
        Char sqlawarn = (Char) o;
        sqlawarn.setCharAt(0, 'W');
        sqlawarn.setCharAt(index - 1, 'W');
    }
}
