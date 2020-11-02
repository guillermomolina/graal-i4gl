package org.guillermomolina.i4gl.runtime.values;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import org.guillermomolina.i4gl.I4GLLanguage;
import org.guillermomolina.i4gl.runtime.context.I4GLContext;
import org.guillermomolina.i4gl.runtime.database.SquirrelDataSet;
import org.guillermomolina.i4gl.runtime.database.SquirrelExecuterHandler;
import org.guillermomolina.i4gl.runtime.database.SquirrelSession;
import org.guillermomolina.i4gl.runtime.types.complex.I4GLCursorType;

import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;

@ExportLibrary(InteropLibrary.class)
public final class I4GLCursor implements TruffleObject {

    private final String sql;
    private I4GLDatabase database;
    private SquirrelDataSet dataSet;
    private List<String> columnLabels;

    public I4GLCursor(final I4GLDatabase database, final String sql) {
        this.database = database;
        this.sql = sql;
        this.columnLabels = new ArrayList<>();
    }

    public void start() {
        database.connect();

        SquirrelSession session = database.getSession();
        SquirrelExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);
        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);
        sqlExecuterTask.run();
        dataSet = sqlExecuterHandlerProxy.getResultSet();
        columnLabels = dataSet.getColumnLabels();

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

    @ExportMessage
    boolean hasMembers() {
        return !columnLabels.isEmpty() && dataSet.isValid();
    }

    @ExportMessage(name = "isMemberReadable")
    @TruffleBoundary
    boolean hasMember(String name) {
        return columnLabels.contains(name);
    }

    @ExportMessage
    boolean isMemberInsertable(String name) {
        return false;
    }

    @ExportMessage
    boolean isMemberModifiable(String name) {
        return false;
    }

    @ExportMessage
    boolean isMemberRemovable(String name) {
        return false;
    }

    @ExportMessage
    @TruffleBoundary
    Object readMember(String name) throws UnknownIdentifierException {
        final int index = columnLabels.indexOf(name);
        if (index == -1) {
            throw UnknownIdentifierException.create(name);
        }
        final Object[] row = dataSet.getCurrentRow();
        return row[index];
    }

    @TruffleBoundary
    private static UnsupportedMessageException unsupported() {
        return UnsupportedMessageException.create();
    }

    @ExportMessage
    @TruffleBoundary
    void writeMember(String name, Object value) throws UnsupportedMessageException {
        throw unsupported();
    }

    @ExportMessage
    @TruffleBoundary
    void removeMember(String name) throws UnsupportedMessageException {
        throw unsupported();
    }

    @ExportMessage
    @TruffleBoundary
    Object getMembers(boolean includeInternal) {
        return new CursorNamesObject(columnLabels.toArray());
    }

    @ExportLibrary(InteropLibrary.class)
    static final class CursorNamesObject implements TruffleObject {

        private final Object[] names;

        CursorNamesObject(Object[] names) {
            this.names = names;
        }

        @ExportMessage
        boolean hasArrayElements() {
            return true;
        }

        @ExportMessage
        boolean isArrayElementReadable(long index) {
            return index >= 0 && index < names.length;
        }

        @ExportMessage
        long getArraySize() {
            return names.length;
        }

        @ExportMessage
        Object readArrayElement(long index) throws InvalidArrayIndexException {
            if (!isArrayElementReadable(index)) {
                CompilerDirectives.transferToInterpreter();
                throw InvalidArrayIndexException.create(index);
            }
            return names[(int) index];
        }
    }
}
