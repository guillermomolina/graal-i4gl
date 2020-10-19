package org.guillermomolina.i4gl.runtime.values;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.runtime.database.SquirrelExecuterHandler;
import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;

import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

@CompilerDirectives.ValueType
public class I4GLDatabase {

    private final String aliasName;
    private SquirrelSession session;

    public I4GLDatabase(final String aliasName) {
        this.aliasName = aliasName;
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

    public Object[] execute(final String sql) {
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
        ResultSetDataSet rsds = sqlExecuterHandlerProxy.getResultSetDataSet();
        if (rsds.currentRowCount() != 1) {
            final String query = sql.replace("\n", "").replace("\r", "").replace("\t", "");
            //throw new DatabaseException(
            //        "The query \"" + query + "\" has not returned exactly one row.");
        }
        ColumnDisplayDefinition[] cDefinitions = rsds.getDataSetDefinition().getColumnDefinitions();
        List<Object[]> rows = rsds.getAllDataForReadOnly();
        return toI4GLArray(cDefinitions, rows.get(0));
    }
}
