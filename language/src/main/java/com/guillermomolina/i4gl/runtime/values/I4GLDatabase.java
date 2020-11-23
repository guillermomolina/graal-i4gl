package com.guillermomolina.i4gl.runtime.values;

import java.sql.SQLException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import com.guillermomolina.i4gl.I4GLLanguage;
import com.guillermomolina.i4gl.runtime.context.I4GLContext;
import com.guillermomolina.i4gl.runtime.database.SquirrelSession;
import com.guillermomolina.i4gl.runtime.exceptions.DatabaseConnectionException;
import com.guillermomolina.i4gl.runtime.types.complex.I4GLDatabaseType;

@ExportLibrary(InteropLibrary.class)
public final class I4GLDatabase implements TruffleObject {

    private final String alias;
    private SquirrelSession session;

    public I4GLDatabase(final String alias) {
        this.alias = alias;
    }

    public SquirrelSession getSession() {
        return session;
    }

    public void connect() {
        if (session == null) {
            session = new SquirrelSession(alias);
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


    @Override
    public String toString() {
        return alias;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(boolean allowSideEffects) {
        return toString();
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<I4GLContext>> getLanguage() {
        return I4GLLanguage.class;
    }

    @ExportMessage
    boolean hasMetaObject() {
        return true;
    }

    @ExportMessage
    Object getMetaObject() {
        return new I4GLDatabaseType(alias);
    }
}
