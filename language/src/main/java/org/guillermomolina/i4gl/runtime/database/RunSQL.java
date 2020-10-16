package org.guillermomolina.i4gl.runtime.database;

import java.io.IOException;

import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;

public final class RunSQL {
    public static void main(String[] args) throws IOException {
        try {
            System.setProperty("squirrel.home", "/opt/squirrel-sql/");

            SquirrelSession session = new SquirrelSession("test");

            ISQLExecuterHandler sqlExecuterHandlerProxy = new SquirrelExecuterHandler(session);

            final String sql = "SELECT COUNT(*) FROM customers";
            SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(session, sql, sqlExecuterHandlerProxy);
            sqlExecuterTask.setExecuteEditableCheck(false);

            sqlExecuterTask.run();
            session.close();
        } catch (Exception e) {
            System.err.println("Failed to close connection " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
