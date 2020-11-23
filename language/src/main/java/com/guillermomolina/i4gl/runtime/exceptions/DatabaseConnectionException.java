package com.guillermomolina.i4gl.runtime.exceptions;

public class DatabaseConnectionException extends I4GLRuntimeException {
    private static final long serialVersionUID = -1098339846624051557L;

    public DatabaseConnectionException() {
        super("Could not connect to database");
    }
    
}
