package org.guillermomolina.i4gl.runtime.values;

import java.sql.SQLException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.database.SquirrelExecuterHandler;
import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.types.complex.I4GLCursorType;

import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;

@ExportLibrary(InteropLibrary.class)
public final class I4GLCursor implements TruffleObject {

    private final String sql;
    private int index;
    private int count;
    private I4GLDatabase database;
    private ResultSetWrapper resultSet;

    public I4GLCursor(final String sql) {
        this.sql = sql;
        this.count = 10;
        this.database = new I4GLDatabase("test");
    }

    public void start() {
        database.connect();
        index = 0;

        SquirrelSession session = database.getSession();
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
        resultSet = sqlExecuterHandlerProxy.getResultSetWrapper();
    }

    public boolean hasNext() {
        try {
            return resultSet.getResultSet().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public Object[] getNext() {
        Object[] result = new Object[2];
        result[0] = index;
        result[1] = new I4GLVarchar(50, "Mundo");
        index++;
        return result;
    }

    public void end() {
        index = count;
    }

    @Override
    public String toString() {
        return sql;
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
        return I4GLCursorType.SINGLETON;
    }

}
