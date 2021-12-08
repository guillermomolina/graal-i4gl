package i4gl.runtime.database;

import i4gl.runtime.values.Char;
import i4gl.runtime.values.IntArray;
import i4gl.runtime.values.Record;

public class SquirrelSqlcaHandler {
    private Record sqlca;
    private Char sqlerrm;
    private Char sqlerrp;
    private IntArray sqlerrd;
    private Char sqlawarn;

    public SquirrelSqlcaHandler(Record sqlca) {
        this.sqlca = sqlca;
        if(sqlca != null) {
            this.sqlerrm = (Char) sqlca.get("sqlerrm");
            this.sqlerrp = (Char) sqlca.get("sqlerrp");
            this.sqlerrd = (IntArray) sqlca.get("sqlerrd");
            this.sqlawarn = (Char) sqlca.get("sqlawarn");
            reset();
        }
    }

    private void reset() {
        if(sqlca != null) {
            sqlca.put("sqlcode", 0);
            sqlerrm.fill(' ');
            sqlerrp.fill(' ');
            sqlerrd.fill(0);
            sqlawarn.fill(' ');
        }
    }

    public void setSqlCode(int errorCode) {
        if(sqlca != null) {
            sqlca.put("sqlcode", 0);   
        }
    }

    public void setSqlErrD(int index, int data) {
        if(sqlerrd != null) {
            sqlerrd.setValueAt(index, data);    
        }
    }

    public void setSqlErrM(String message) {
        if(sqlca != null) {
            sqlca.put("sqlerrm", new Char(message));   
        }
    }

    public void setSqlAWarn(int index) {
        if(sqlawarn != null) {
            sqlawarn.setCharAt(0, 'W');
            sqlawarn.setCharAt(index, 'W');
        }
    }
}