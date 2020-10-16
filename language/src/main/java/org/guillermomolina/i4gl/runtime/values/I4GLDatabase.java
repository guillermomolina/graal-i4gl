package org.guillermomolina.i4gl.runtime.values;

import java.sql.SQLException;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;

@CompilerDirectives.ValueType
public class I4GLDatabase {

    private final SquirrelSession session;

    public I4GLDatabase(final String aliasName) {
        session = new SquirrelSession(aliasName);
    }

    public void close() {
        try {
            session.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
         }
    }
}
