package org.guillermomolina.i4gl.squirrel;

import java.util.Iterator;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

public class SquirrelCli {

   private static CliConnectionData _cliConnectionData = new CliConnectionData();

   public static void connect(String aliasName) {
      connect(aliasName, null);
   }

   public static void connect(String aliasName, String password) {
      Iterator<? extends ISQLAlias> aliasIterator = Main.getApplication().getAliasesAndDriversManager().aliases();

      while (aliasIterator.hasNext()) {
         ISQLAlias alias = aliasIterator.next();

         if (aliasName.equals(alias.getName())) {
            _cliConnectionData.setAlias(alias);

            if (null != password) {
               try {
                  AliasPasswordHandler.setPassword(_cliConnectionData.getAlias(), password);
               } catch (ValidationException e) {
                  throw new RuntimeException(e);
               }
            }

            _cliConnectionData.createCliSession();

            return;
         }
      }

      throw new IllegalArgumentException("Alias name \"" + aliasName + "\" not found.");
   }

   public static void connect(String url, String user, String password, String driver, String drivercp) {
      try {
         SQLDriver sqlDriver = new SQLDriver(new UidIdentifier());
         sqlDriver.setDriverClassName(driver);

         String classPathSeparator = System.getProperty("path.separator");
         sqlDriver.setJarFileNames(drivercp.split(Pattern.quote(classPathSeparator)));

         sqlDriver.setName("temporaryDriver_" + url + "_" + sqlDriver.getIdentifier().toString());

         Main.getApplication().getAliasesAndDriversManager().addDriver(sqlDriver, NullMessageHandler.getInstance());

         SQLAlias alias = new SQLAlias(new UidIdentifier());

         alias.setName("temporaryAlias_" + url + "_" + sqlDriver.getIdentifier().toString());

         alias.setUrl(url);
         alias.setUserName(user);

         if (null != password) {
            AliasPasswordHandler.setPassword(alias, password);
         }

         alias.setDriverIdentifier(sqlDriver.getIdentifier());

         _cliConnectionData.setAlias(alias);

         _cliConnectionData.createCliSession();

      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }

   public static void setMaxRows(int maxRows) {
      if (0 < maxRows) {
         Main.getApplication().getSquirrelPreferences().getSessionProperties().setSQLLimitRows(true);
         Main.getApplication().getSquirrelPreferences().getSessionProperties().setSQLNbrRowsToShow(maxRows);
      } else {
         Main.getApplication().getSquirrelPreferences().getSessionProperties().setSQLLimitRows(false);
      }

      try {
         // We clear the current Session for the changes to take effect in the next
         // exec() call.
         _cliConnectionData.closeCliSession();
      } catch (Exception e) {
         //
      }
   }

   public static void exec(String sql) {
      exec(sql, null);
   }

   public static void exec(String sql, String outputFile) {
      sql = CLISqlFileHandler.handleOptionalSqlFile(sql, true);
      outputAsText(sql, outputFile);
   }

   private static void outputAsText(String sql, String outputFile) {
      _cliConnectionData.ensureCliSessionCreated();

      ISQLExecuterHandler sqlExecuterHandlerProxy = new CliSQLExecuterHandler(_cliConnectionData.getCliSession(),
            outputFile);

      SQLExecuterTask sqlExecuterTask = new SQLExecuterTask(_cliConnectionData.getCliSession(), sql,
            sqlExecuterHandlerProxy);
      sqlExecuterTask.setExecuteEditableCheck(false);

      sqlExecuterTask.run();
   }

   public static void close() {
      try {
         System.err.println("Database connection closed. Alias is still valid. Next exec() call will reconnect.");
      } catch (Exception e) {
         System.err.println("Failed to close connection " + e.getClass().getName() + ": " + e.getMessage());
      }
   }
}
