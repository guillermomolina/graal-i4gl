package org.guillermomolina.i4gl.runtime.database;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;

import org.guillermomolina.i4gl.exceptions.NotImplementedException;
import org.guillermomolina.i4gl.runtime.exceptions.DatabaseException;

import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class SquirrelExecuterHandler implements ISQLExecuterHandler {
   private SquirrelSession session;

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
      //
   }

   @Override
   public void sqlToBeExecuted(String sql) {
      //
   }

   @Override
   public void sqlDataUpdated(int updateCount) {
      System.out.println(updateCount + " rows updated");
   }

   @Override
   public void sqlResultSetAvailable(ResultSetWrapper rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
         throws DataSetException {

      ResultSetDataSet rsds = new ResultSetDataSet();
      rsds.setLimitDataRead(true);

      DialectType dialectType = DialectFactory.getDialectType(session.getMetaData());

      rsds.setSqlExecutionTabResultSet(rst, null, dialectType);

      ResultAsText resultAsText = new ResultAsText(rsds.getDataSetDefinition().getColumnDefinitions(), true);

      for (Object[] row : rsds.getAllDataForReadOnly()) {
         resultAsText.addRow(row);
      }
   }

   @Override
   public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount) {
      System.out.println("Execution took " + info.getTotalElapsedMillis() + " Millis");
   }

   @Override
   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement) {
      if (!sqlExecErrorMsgs.isEmpty()) {
         System.out.println(sqlExecErrorMsgs.get(0));
      }
   }
}
