package org.guillermomolina.i4gl.runtime.values;

import java.sql.SQLException;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.runtime.database.SquirrelExecuterHandler;
import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;

import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

@CompilerDirectives.ValueType
public class I4GLDatabase {

    private final String aliasName;
    private SquirrelSession session;

    public I4GLDatabase(final String aliasName) {
        this.aliasName = aliasName;
    }

    public void connect() {
        if(session == null) {
            session = new SquirrelSession(aliasName);
        }
    }

    public void close() {
        if (session != null) {
            try {
                session.close();
                session = null;
            } catch (SQLException e) {
                throw new DatabaseConnectionException();
            }
        }
    }

    public void execute(final String sql) {
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
        ResultSetDataSet rsds = sqlExecuterHandlerProxy.getResultSetDataSet();
        for(Object[] row: rsds.getAllDataForReadOnly()) {
            System.out.println(Arrays.toString(row));
        }
    }
}
