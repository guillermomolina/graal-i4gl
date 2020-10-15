package org.guillermomolina.i4gl.squirrel;

import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

public final class RunSQL {

    private static CliConnectionData cliConnectionData = new CliConnectionData();

    public static void connect(String aliasName) {
        Iterator<? extends ISQLAlias> aliasIterator = Main.getApplication().getAliasesAndDriversManager().aliases();

        while (aliasIterator.hasNext()) {
            ISQLAlias alias = aliasIterator.next();

            if (aliasName.equals(alias.getName())) {
                cliConnectionData.setAlias(alias);
                cliConnectionData.createCliSession();

                return;
            }
        }

        throw new IllegalArgumentException("Alias name \"" + aliasName + "\" not found.");
    }

    public static void exec(String sql) {
        cliConnectionData.ensureCliSessionCreated();

        ISQLExecuterHandler sqlExecuterHandlerProxy = new CliSQLExecuterHandler(cliConnectionData.getCliSession(),
                null);

        SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(cliConnectionData.getCliSession(), sql,
                sqlExecuterHandlerProxy);
        sqlExecuterTask.setExecuteEditableCheck(false);

        sqlExecuterTask.run();
    }

    public static void close() {
        try {
            cliConnectionData.closeCliSession();
        } catch (Exception e) {
            System.err.println("Failed to close connection " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("squirrel.home", "/opt/squirrel-sql/");

        CliInitializer.initializeSquirrelInCliMode();

        connect("oartdb");
        exec("SELECT * FROM DUALMYSQL");
        close();
    }
}
