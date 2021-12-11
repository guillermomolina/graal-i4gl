package i4gl.runtime.database;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.logging.Level;

import com.oracle.truffle.api.TruffleLogger;

import i4gl.I4GLLanguage;
import i4gl.exceptions.DatabaseException;
import i4gl.exceptions.NotImplementedException;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class SquirrelExecuterHandler implements ISQLExecuterHandler {
   private static final TruffleLogger LOGGER = I4GLLanguage.getLogger(SquirrelExecuterHandler.class);
   private final SquirrelSession session;
   private SquirrelDataSet resultSet;

   public SquirrelExecuterHandler(SquirrelSession session) {
      this.session = session;
   }

   @Override
   public void sqlExecutionCancelled() {
      throw new NotImplementedException();
   }

   @Override
   public void sqlExecutionWarning(SQLWarning warn) {
      throw new NotImplementedException();
   }

   @Override
   public String sqlExecutionException(Throwable th, String postErrorString) {
      String retMessage = postErrorString;

      if (postErrorString != null) {
         throw new DatabaseException(postErrorString);
      }

      if (th != null) {
         if (th instanceof SQLException) {
            final String message = Utilities.getSqlExecutionErrorMessage((SQLException) th);
            throw new DatabaseException(message);
         } else {
            throw new DatabaseException(th.getMessage());
         }
      }

      return retMessage;
   }

   @Override
   public void sqlStatementCount(int statementCount) {
      LOGGER.log(Level.FINE, "SQL statement count: {0}", statementCount);
   }

   @Override
   public void sqlToBeExecuted(String sql) {
      LOGGER.log(Level.FINE, "SQL to be executed: \"{0}\"", sql);
   }

   @Override
   public void sqlDataUpdated(int updateCount) {
      LOGGER.log(Level.FINE, "{0} rows updated", updateCount);
   }

   @Override
   public void sqlResultSetAvailable(ResultSetWrapper rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
         throws DataSetException {
      resultSet = new SquirrelDataSet();
      DialectType dialectType = DialectFactory.getDialectType(session.getMetaData());
      resultSet.setSqlExecutionTabResultSet(rst, null, dialectType);
   }

   public SquirrelDataSet getResultSet() {
      return resultSet;
   }

   @Override
   public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount) {
      LOGGER.log(Level.FINE, "Execution took {0} Millis", info.getTotalElapsedMillis());
   }

   @Override
   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement) {
      if (!sqlExecErrorMsgs.isEmpty()) {
         for(String msg: sqlExecErrorMsgs) {
            LOGGER.severe(msg);
         }
      }
   }
}
