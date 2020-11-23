package com.guillermomolina.i4gl.runtime.database;

import java.sql.SQLException;
import java.util.Iterator;

import com.guillermomolina.i4gl.exceptions.NotImplementedException;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;

public class SquirrelSession extends SquirrelSessionAdapter {
   private final SQLConnection sqlConnection;
   private final QueryTokenizer tokenizer;
   private final SessionProperties sessionProperties;

   public SquirrelSession(String aliasName) {
      SquirrelInitializer.initialize();

      ISQLAlias aliasToConnectTo = findAlias(aliasName);
      IIdentifier driverID = aliasToConnectTo.getDriverIdentifier();
      ISQLDriver sqlDriver = Main.getApplication().getAliasesAndDriversManager().getDriver(driverID);

      SQLDriverManager sqlDriverManager = Main.getApplication().getSQLDriverManager();

      SQLDriverPropertyCollection props = aliasToConnectTo.getDriverPropertiesClone();

      if (!aliasToConnectTo.getUseDriverProperties()) {
         props.clear();
      }

      sqlConnection = sqlDriverManager.getConnection(sqlDriver, aliasToConnectTo, aliasToConnectTo.getUserName(),
            AliasPasswordHandler.getPassword(aliasToConnectTo), props);

      sessionProperties = Main.getApplication().getSquirrelPreferences().getSessionProperties();
      sessionProperties.setSQLLimitRows(false);
      sessionProperties.setSQLReadOn(true);
      tokenizer = new QueryTokenizer(sessionProperties.getSQLStatementSeparator(),
            sessionProperties.getStartOfLineComment(), sessionProperties.getRemoveMultiLineComment());
   }

   public ISQLAlias findAlias(String aliasName) {
      Iterator<? extends ISQLAlias> aliasIterator = Main.getApplication().getAliasesAndDriversManager().aliases();

      while (aliasIterator.hasNext()) {
         ISQLAlias alias = aliasIterator.next();

         if (aliasName.equals(alias.getName())) {
            return (alias);
         }
      }

      throw new IllegalArgumentException("Squirrel alias name \"" + aliasName + "\" not found.");
   }

   @Override
   public ISQLConnection getSQLConnection() {
      return sqlConnection;
   }

   @Override
   public ISQLDatabaseMetaData getMetaData() {
      return sqlConnection.getSQLMetaData();
   }

   @Override
   public IQueryTokenizer getQueryTokenizer() {
      return tokenizer;
   }

   @Override
   public SessionProperties getProperties() {
      return sessionProperties;
   }

   @Override
   public void close() throws SQLException {
      // Close on sqlConnection will start threads
      // that will prevent ending the process.
      sqlConnection.getConnection().close();
   }

   @Override
   public void showMessage(Throwable th) {
      throw new NotImplementedException();
   }

   @Override
   public void showMessage(String msg) {
      throw new NotImplementedException();
   }

   @Override
   public void showErrorMessage(Throwable th) {
      throw new NotImplementedException();
   }

   @Override
   public void showErrorMessage(String msg) {
      throw new NotImplementedException();
   }

   @Override
   public void showWarningMessage(String msg) {
      throw new NotImplementedException();
   }

   @Override
   public void showWarningMessage(Throwable th) {
      throw new NotImplementedException();
   }
}
