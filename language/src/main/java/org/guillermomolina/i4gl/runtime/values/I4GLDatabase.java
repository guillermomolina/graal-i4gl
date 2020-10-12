package org.guillermomolina.i4gl.runtime.values;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.oracle.truffle.api.CompilerDirectives;

import org.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;
import org.guillermomolina.i4gl.runtime.exceptions.JDBCDriverClassNotFoundException;

@CompilerDirectives.ValueType
public class I4GLDatabase {

    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public I4GLDatabase(final String driver, final String url, final String user, final String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.connection = null;
    }

    public I4GLDatabase() {
        this("org.h2.Driver", "jdbc:h2:file:~/test", "sa", "");
    }

    public void connect() {
        assert connection == null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new JDBCDriverClassNotFoundException();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
         }
    }
}
