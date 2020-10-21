package org.guillermomolina.i4gl.runtime.values;

import java.sql.SQLException;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.runtime.database.SquirrelDataSet;
import org.guillermomolina.i4gl.runtime.database.SquirrelExecuterHandler;
import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseException;

import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;

@CompilerDirectives.ValueType
public class I4GLDatabase {

    private final String aliasName;
    private SquirrelSession session;

    public I4GLDatabase(final String aliasName) {
        this.aliasName = aliasName;
    }

    public SquirrelSession getSession() {
        return session;
    }

    public void connect() {
        if (session == null) {
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
/*
    public static Object toI4GLType(final ColumnDisplayDefinition cDefinition, final Object sqlValue) {
        if (sqlValue == null) {
            return I4GLNull.SINGLETON;
        }
        switch (cDefinition.getSqlType()) {
            case Types.VARCHAR:
                return new I4GLVarchar(cDefinition.getPrecision(), (String) sqlValue);
            case Types.DECIMAL:
                return new I4GLDecimal((BigDecimal) sqlValue);
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.REAL:
            case Types.FLOAT:
                return sqlValue;
            default:
                throw new NotImplementedException();
        }
    }

    public static Object[] toI4GLArray(final ColumnDisplayDefinition[] cDefinitions, final Object[] row) {
        Object[] values = new Object[row.length];
        for (int i = 0; i < row.length; i++) {
            values[i] = toI4GLType(cDefinitions[i], row[i]);
        }
        return values;
    }
*/
    public Object[] execute(final String sql) {
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
        Object[] result = null;
        SquirrelDataSet rsds = sqlExecuterHandlerProxy.getResultSet();
        if(rsds.next()) {
            result = rsds.getCurrentRow();
        }
        if (rsds.next()) {
            final String query = sql.replace("\n", "").replace("\r", "").replace("\t", "");
            throw new DatabaseException("The query \"" + query + "\" has not returned exactly one row.");
        }
        return result;
    }
}
