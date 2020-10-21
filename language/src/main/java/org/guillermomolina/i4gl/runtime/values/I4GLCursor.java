package org.guillermomolina.i4gl.runtime.values;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLContext;
import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.database.SquirrelDataSet;
import org.guillermomolina.i4gl.runtime.database.SquirrelExecuterHandler;
import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.types.complex.I4GLCursorType;

import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

@ExportLibrary(InteropLibrary.class)
public final class I4GLCursor implements TruffleObject {

    private final String sql;
    private I4GLDatabase database;
    private SquirrelDataSet dataSet;
    private ColumnDisplayDefinition[] columnDefinitions;

    public I4GLCursor(final String sql) {
        this.sql = sql;
        this.database = new I4GLDatabase("test");
    }

    public void start() {
        database.connect();

        SquirrelSession session = database.getSession();
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
        dataSet = sqlExecuterHandlerProxy.getResultSet();
        columnDefinitions = dataSet.getDataSetDefinition().getColumnDefinitions();
    }

    public boolean next() {
        return dataSet.next();
    }

    public Object[] getRow() {
        return dataSet.getCurrentRow();
    }

    public void end() {
        dataSet.closeStatementAndResultSet();
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
