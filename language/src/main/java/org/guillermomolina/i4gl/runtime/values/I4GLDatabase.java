package org.guillermomolina.i4gl.runtime.values;

import java.sql.SQLException;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;

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
}
