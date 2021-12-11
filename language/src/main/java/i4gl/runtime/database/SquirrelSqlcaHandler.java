package i4gl.runtime.database;

import com.oracle.truffle.api.interop.InvalidArrayIndexException;

import i4gl.runtime.values.Array;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Record;

public class SquirrelSqlcaHandler {
    private Record sqlca;
    private Char sqlerrm;
    private Char sqlerrp;
    private Array sqlerrd;
    private Char sqlawarn;

    public SquirrelSqlcaHandler(Record sqlca) {
        this.sqlca = sqlca;
        if(sqlca != null) {
            this.sqlerrm = (Char) sqlca.getObject("sqlerrm");
            this.sqlerrp = (Char) sqlca.getObject("sqlerrp");
            this.sqlerrd = (Array) sqlca.getObject("sqlerrd");
            this.sqlawarn = (Char) sqlca.getObject("sqlawarn");
            reset();
        }
    }

    private void reset() {
        if(sqlca != null) {
            sqlca.putObject("sqlcode", 0);
            sqlerrm.fill(' ');
            sqlerrp.fill(' ');
            sqlerrd.fill(0);
            sqlawarn.fill(' ');
        }
    }

    public void setSqlCode(int errorCode) {
        if(sqlca != null) {
            sqlca.putObject("sqlcode", 0);   
        }
    }

    public void setSqlErrD(int index, int data) throws InvalidArrayIndexException {
        if(sqlerrd != null) {
            sqlerrd.setValueAt(index, data);    
        }
    }

    public void setSqlErrM(String message) {
        if(sqlca != null) {
            sqlca.putObject("sqlerrm", new Char(message));   
        }
    }

    public void setSqlAWarn(int index) {
        if(sqlawarn != null) {
            sqlawarn.setCharAt(0, 'W');
            sqlawarn.setCharAt(index, 'W');
        }
    }
}