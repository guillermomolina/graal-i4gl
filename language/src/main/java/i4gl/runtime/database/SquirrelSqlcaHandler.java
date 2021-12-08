package i4gl.runtime.database;

import i4gl.runtime.values.I4GLChar;
import i4gl.runtime.values.I4GLIntArray;
import i4gl.runtime.values.I4GLRecord;

public class SquirrelSqlcaHandler {
    private I4GLRecord sqlca;
    private I4GLChar sqlerrm;
    private I4GLChar sqlerrp;
    private I4GLIntArray sqlerrd;
    private I4GLChar sqlawarn;

    public SquirrelSqlcaHandler(I4GLRecord sqlca) {
        this.sqlca = sqlca;
        if(sqlca != null) {
            this.sqlerrm = (I4GLChar) sqlca.get("sqlerrm");
            this.sqlerrp = (I4GLChar) sqlca.get("sqlerrp");
            this.sqlerrd = (I4GLIntArray) sqlca.get("sqlerrd");
            this.sqlawarn = (I4GLChar) sqlca.get("sqlawarn");
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
            sqlca.put("sqlerrm", new I4GLChar(message));   
        }
    }

    public void setSqlAWarn(int index) {
        if(sqlawarn != null) {
            sqlawarn.setCharAt(0, 'W');
            sqlawarn.setCharAt(index, 'W');
        }
    }
}