package i4gl.runtime.database;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import i4gl.exceptions.NotImplementedException;
import i4gl.runtime.types.compound.CharType;
import i4gl.runtime.types.compound.DecimalType;
import i4gl.runtime.types.compound.VarcharType;
import i4gl.runtime.values.Char;
import i4gl.runtime.values.Date;
import i4gl.runtime.values.Decimal;
import i4gl.runtime.values.Null;
import i4gl.runtime.values.Sqlca;
import i4gl.runtime.values.Varchar;
import net.sourceforge.squirrel_sql.fw.datasetviewer.BlockMode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SquirrelDataSet implements IDataSet {
   private final static ILogger s_log = LoggerController.createLogger(SquirrelDataSet.class);

   // These 2 should be handled with an Iterator.
   private int _iCurrent = -1;

   private Object[] _currentRow;

   private int _columnCount;

   private DataSetDefinition _dataSetDefinition;

   /**
    * If <TT>true</TT> cancel has been requested.
    */
   private volatile boolean _cancel = false;

   /**
    * the result set reader, which we will notify of cancel requests
    */
   private ResultSetReader _rdr = null;

   /**
    * The type of dialect of the session from which this data set came. Plugins can
    * now override behavior for standard SQL types, so it is necessary to know the
    * current dialect so that the correct plugin DataTypeComponent can be chosen
    * for rendering this dataset, if one has been registered.
    */
   private DialectType _dialectType = null;

   private final TableColumnInfo[] tableColumnInfos;

   private boolean _limitDataRead = false;

   /**
    * Default constructor.
    *
    * @param tableColumnInfos
    */
   public SquirrelDataSet(TableColumnInfo[] tableColumnInfos) {
      super();
      this.tableColumnInfos = tableColumnInfos;
   }

   public SquirrelDataSet() {
      this.tableColumnInfos = new TableColumnInfo[] {};
   }

   /**
    * Form used by Tabs other than ContentsTab
    *
    * @param rs          the ResultSet to set.
    * @param dialectType the type of dialect in use.
    * @throws DataSetException
    */
   public int setResultSet(ResultSet rs, DialectType dialectType) throws DataSetException {
      return _setResultSet(new ResultSetWrapper(rs), null, null, false, false, dialectType);
   }

   /**
    * Content Tab may wish to limit data read for big columns.
    *
    * @param limitDataRead
    */
   public void setLimitDataRead(boolean limitDataRead) {
      this._limitDataRead = limitDataRead;
   }

   /**
    * Form used by ContentsTab, and for SQL results
    *
    * @param rs            the ResultSet to set.
    * @param fullTableName the fully-qualified table name
    * @param dialectType   the type of dialect in use.
    * @throws DataSetException
    */
   public int setContentsTabResultSet(ResultSet rs, String fullTableName, DialectType dialectType)
         throws DataSetException {
      return _setResultSet(new ResultSetWrapper(rs), fullTableName, null, false, true, dialectType);
   }

   public int setSqlExecutionTabResultSet(ResultSetWrapper rs, String fullTableName, DialectType dialectType)
         throws DataSetException {
      return _setResultSet(rs, fullTableName, null, false, true, dialectType);
   }

   /**
    * External method to read the contents of a ResultSet that is used by all Tab
    * classes except ContentsTab. This tunrs all the data into strings for
    * simplicity of operation.
    */
   public int setResultSet(ResultSet rs, int[] columnIndices, boolean computeWidths, DialectType dialectType)
         throws DataSetException {
      return _setResultSet(new ResultSetWrapper(rs), null, columnIndices, computeWidths, false, dialectType);
   }

   /**
    * Internal method to read the contents of a ResultSet that is used by all Tab
    * classes
    *
    * @return The number of rows read from the ResultSet
    */
   private int _setResultSet(ResultSetWrapper rs, String fullTableName, int[] columnIndices, boolean computeWidths,
         boolean useColumnDefs, DialectType dialectType) throws DataSetException {
      reset();
      _dialectType = dialectType;

      if (columnIndices != null && columnIndices.length == 0) {
         columnIndices = null;
      }

      _iCurrent = -1;

      if (rs == null) {
         return 0;
      }

      try {
         ResultSetMetaData md = rs.getResultSet().getMetaData();
         _columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();

         // Done before actually reading the data from the ResultSet. If done
         // after
         // reading the data from the ResultSet Oracle throws a
         // NullPointerException
         // when processing ResultSetMetaData methods for the ResultSet
         // returned for
         // DatabasemetaData.getExportedKeys.
         ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md, fullTableName, columnIndices, computeWidths);

         _dataSetDefinition = new DataSetDefinition(colDefs, columnIndices);

         // Read the entire row, since some drivers complain if columns are
         // read out of sequence
         _rdr = new ResultSetReader(rs, dialectType);

         return 0;

         // ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md,
         // columnIndices, computeWidths);
         // _dataSetDefinition = new DataSetDefinition(colDefs);
      } catch (SQLException ex) {
         // Don't log an error message here. It is possible that the user
         // interrupted the query because it was taking too long. Just
         // throw the exception, and let the caller decide whether or not
         // the exception should be logged.
         throw new DataSetException(ex);
      }
   }

   public static Object toI4GLObject(final ColumnDisplayDefinition cDefinition, final Object sqlValue) {
      if (sqlValue == null) {
         return Null.SINGLETON;
      }
      switch (cDefinition.getSqlType()) {
         case Types.CHAR:
            CharType charType = new CharType(cDefinition.getPrecision());
            return new Char(charType, (String) sqlValue);
         case Types.VARCHAR:
            VarcharType varcharType = new VarcharType(cDefinition.getPrecision());
            return new Varchar(varcharType, (String) sqlValue);
         case Types.DECIMAL:
            DecimalType decimalType = new DecimalType(cDefinition.getPrecision(), cDefinition.getScale());
            return new Decimal(decimalType, (BigDecimal) sqlValue);
         case Types.DATE:
            return new Date((java.sql.Date) sqlValue);
         case Types.INTEGER:
         case Types.BIGINT:
         case Types.REAL:
         case Types.FLOAT:
            return sqlValue;
         default:
            throw new NotImplementedException();
      }
   }

   private Object[] createRow(int[] columnIndices, ColumnDisplayDefinition[] colDefs,
         BlockMode blockMode) throws SQLException {
      Object[] row = _rdr.readRow(colDefs, blockMode, _limitDataRead);

      if (row == null) {
         return null;
      }

      for (int i = 0; i < row.length; i++) {
         row[i] = toI4GLObject(colDefs[i], row[i]);
      }

      // Now reorder columns.
      // This is used by ObjecTree tabs to define the
      // order columns displaying connection meta data are displayed.
      if (columnIndices != null) {
         Object[] newRow = new Object[_columnCount];
         for (int i = 0; i < _columnCount; i++) {
            if (columnIndices[i] - 1 < row.length) {
               newRow[i] = row[columnIndices[i] - 1];
            } else {
               newRow[i] = "Unknown";
            }
         }
         row = newRow;
      }
      return row;
   }

   @Override
   public final int getColumnCount() {
      return _columnCount;
   }

   @Override
   public DataSetDefinition getDataSetDefinition() {
      return _dataSetDefinition;
   }

   @Override
   public synchronized boolean next(IMessageHandler msgHandler) throws DataSetException {
      throw new DataSetException("Do not use this method");
   }

   public synchronized boolean next() {
      Sqlca.SINGLETON.setSqlerrd(5, _iCurrent + 1);
      if (_cancel) {
         _currentRow = null;
         return false;
      }
      try {
         _currentRow = createRow(_dataSetDefinition.getColumnIndices(), _dataSetDefinition.getColumnDefinitions(),
               BlockMode.INDIFFERENT);
      } catch (SQLException e) {
         Sqlca.SINGLETON.setSqlcode(e.getErrorCode());
         Sqlca.SINGLETON.setSqlerrm(e.getMessage());
      }
      if (_currentRow == null) {
         return false;
      }
      ++_iCurrent;
      Sqlca.SINGLETON.setSqlerrd(5, _iCurrent + 1);
      return true;
   }

   public Object[] getCurrentRow() {
      return _currentRow;
   }

   /*
    * (non-Javadoc)
    *
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet#get(int)
    */
   @Override
   public Object get(int columnIndex) {
      if (_currentRow != null) {
         return _currentRow[columnIndex];
      } else {
         return null;
      }
   }

   public void cancelProcessing() {
      _rdr.setStopExecution(true);
      _cancel = true;
   }

   private int[] computeColumnWidths() {
      int[] colWidths = new int[_columnCount];
      if (_currentRow != null) {
         Object[] row = _currentRow;
         for (int col = 0; col < _columnCount; col++) {
            if (row[col] != null) {
               int colWidth = row[col].toString().length();
               if (colWidth > colWidths[col]) {
                  colWidths[col] = colWidth + 2;
               }
            }
         }
      }
      return colWidths;
   }

   // SS: Modified to auto-compute column widths if <computeWidths> is true
   private ColumnDisplayDefinition[] createColumnDefinitions(ResultSetMetaData md, String fullTableName,
         int[] columnIndices, boolean computeWidths) throws SQLException {
      // ColumnDisplayDefinition should also have the Type (String, Date,
      // Double,Integer,Boolean)
      int[] colWidths = null;

      // SS: update dynamic column widths
      if (computeWidths) {
         colWidths = computeColumnWidths();
      }

      ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[_columnCount];
      for (int i = 0; i < _columnCount; ++i) {
         int idx = columnIndices != null ? columnIndices[i] : i + 1;

         // save various info about the column for use in user input validation
         // when editing table contents.
         // Note that the columnDisplaySize is included two times, where the
         // first
         // entry may be adjusted for actual display while the second entry is
         // the
         // size expected by the DB.
         // The isNullable() method returns three values that we convert into
         // two
         // by saying that if it is not known whether or not a column allows
         // nulls,
         // we will allow the user to enter nulls and any problems will be
         // caught
         // when they try to save the data to the DB
         boolean isNullable = true;
         if (md.isNullable(idx) == ResultSetMetaData.columnNoNulls)
            isNullable = false;

         int precis;
         try {
            precis = md.getPrecision(idx);
         } catch (NumberFormatException ignore) {
            precis = Integer.MAX_VALUE; // Oracle throws this ex on BLOB data
            // types
         }

         boolean isSigned = true;
         try {
            isSigned = md.isSigned(idx); // HSQLDB 1.7.1 throws error.
         } catch (SQLException ignore) {
            // Empty block
         }

         boolean isCurrency = false;

         try {
            // Matt Dahlman: this causes problems with the JDBC driver delivered
            // with Teradata V2R05.00.00.11
            isCurrency = md.isCurrency(idx);
         } catch (SQLException e) {
            s_log.error("Failed to call ResultSetMetaData.isCurrency()", e);
         }

         boolean isAutoIncrement = false;
         try {
            isAutoIncrement = md.isAutoIncrement(idx);
         } catch (SQLException e) {
            s_log.error("Failed to call ResultSetMetaData.isAutoIncrement()", e);
         }

         // KLUDGE:
         // We want some info about the columns to be available for validating the
         // user input during cell editing operations. Ideally we would get that
         // info inside the SquirrelDataSet class during the creation of the
         // columnDefinition objects by using various functions in ResultSetMetaData
         // such as isNullable(idx). Unfortunately, in at least some DBMSs (e.g.
         // Postgres, HSDB) the results of those calls are not the same (and are less
         // accurate
         // than) the SQLMetaData.getColumns() call used in ColumnsTab to get the column
         // info.
         // Even more unfortunate is the fact that the set of attributes reported on by
         // the two
         // calls is not the same, with the ResultSetMetadata listing things not provided
         // by
         // getColumns. Most of the data provided by the ResultSetMetaData calls is
         // correct.
         // However, the nullable/not-nullable property is not set correctly in at least
         // two
         // DBMSs, while it is correct for those DBMSs in the getColumns() info.
         // Therefore,
         // we collect the collumn nullability information from getColumns() and pass
         // that
         // info to the ResultSet to override what it got from the ResultSetMetaData.

         if (i < tableColumnInfos.length) {
            TableColumnInfo info = tableColumnInfos[i];
            if (info.isNullAllowed() == DatabaseMetaData.columnNoNulls) {
               isNullable = false;
            }
         }

         String columnName = getColumnName(i, md, idx);
         String columnTypeName = getColumnTypeName(i, md, idx);
         int baseColumnType = getColumnType(i, md, idx);
         int columnType = fixColumnType(columnName, baseColumnType, columnTypeName, _dialectType);

         columnDefs[i] = new ColumnDisplayDefinition(
               computeWidths ? colWidths[i] : Math.min(md.getColumnDisplaySize(idx), 1000),
               fullTableName + ":" + md.getColumnLabel(idx), columnName, md.getColumnLabel(idx), columnType,
               columnTypeName, isNullable, md.getColumnDisplaySize(idx), precis, md.getScale(idx), isSigned, isCurrency,
               isAutoIncrement, _dialectType, createResultSetMetaDataTable(md, idx));
      }
      return columnDefs;
   }

   private ResultMetaDataTable createResultSetMetaDataTable(ResultSetMetaData md, int idx) throws SQLException {
      try {
         if (StringUtilities.isEmpty(md.getTableName(idx))) {
            return null;
         }

         return new ResultMetaDataTable(StringUtilities.emptyToNull(md.getCatalogName(idx)),
               StringUtilities.emptyToNull(md.getSchemaName(idx)), md.getTableName(idx));
      } catch (Exception e) {
         s_log.error("Failed to get table info from ResultSetMetaData.", e);
         return null;
      }
   }

   private String getColumnName(int i, ResultSetMetaData md, int idx) throws SQLException {
      if (i < tableColumnInfos.length) {
         return tableColumnInfos[i].getColumnName();
      }
      return md.getColumnName(idx);
   }

   private String getColumnTypeName(int i, ResultSetMetaData md, int idx) throws SQLException {
      if (i < tableColumnInfos.length) {
         return tableColumnInfos[i].getTypeName();
      }
      return md.getColumnTypeName(idx);
   }

   private int getColumnType(int i, ResultSetMetaData md, int idx) throws SQLException {
      if (i < tableColumnInfos.length) {
         return tableColumnInfos[i].getDataType();
      }
      return md.getColumnType(idx);
   }

   /**
    * The following is a synopsis of email conversations with David Crawshaw, who
    * maintains the SQLite JDBC driver:
    * <p>
    * SQLite's JDBC driver returns Types.NULL as the column type if the table has
    * no rows. Columns don't necessarily have a type attribute; the type is
    * associated with the values in the column (this is referred to as manifest
    * typing). Columns can have an affinity (a preferred storage option) which
    * looks just like a type in the create table statement; however, it can be
    * whatever the user chooses, and not necessarily a standard SQL type. Even
    * still, SQLite exposes no API call to retrieve the column affinity (or storage
    * clause). However, it does make the type name that the user used available and
    * that may possibly be a valid standard SQL type.
    * <p>
    * So, if the specified column type code is Types.NULL, this method attempts to
    * adjust the type code from Types.NULL to a sensible Type based on the column
    * type name reported by the driver. If the column type name doesn't match
    * (ignoring case) an existing JDBC type, then this method returns
    * Types.VARCHAR.
    *
    * For the same reason as described above, numeric types are interpreted as
    * INTEGER, when they might be NUMERIC and actually overflow.
    *
    * @param columnName     the name of the column
    * @param columnType     the type code that was given by the jdbc driver.
    * @param columnTypeName the type name of the column that was given by the jdbc
    *                       driver
    * @param dialectType
    * @return a type code that is not Types.NULL.
    */
   private int fixColumnType(String columnName, int columnType, String columnTypeName, DialectType dialectType) {
      int result = columnType;
      if (columnType == Types.NULL) {
         result = JDBCTypeMapper.getJdbcType(columnTypeName);
         if (result == Types.NULL) {
            result = Types.VARCHAR;
         }
      }
      /*
       * else if ( DialectType.SQLLITE == dialectType ) { if (columnType ==
       * Types.INTEGER && "NUMERIC".equals(columnTypeName)) { result = Types.NUMERIC;
       * } }
       */

      if (result != columnType) {
         if (s_log.isDebugEnabled()) {
            s_log.debug("Converting type code for column " + columnName + ". Original column type code and name were "
                  + columnType + " (see code-constants in java.sql.Types) and " + columnTypeName + "; New type code is "
                  + result);
         }
      }
      return result;
   }

   private void reset() {
      _iCurrent = -1;
      _currentRow = null;
      _columnCount = 0;
      _dataSetDefinition = null;
   }

   public void resetCursor() {
      _iCurrent = -1;
      _currentRow = null;
   }

   public List<String> getColumnLabels() {
      if (_dataSetDefinition == null) {
         return null;
      }

      List<String> columnNames = new ArrayList<>(getColumnCount());
      for (ColumnDisplayDefinition colDef : _dataSetDefinition.getColumnDefinitions()) {
         String columnName = "UNKNOWN";
         if (colDef != null) {
            columnName = colDef.getLabel();
         }
         columnNames.add(columnName);
      }
      return columnNames;
   }

   @Override
   public String toString() {
      StringBuilder result = new StringBuilder();
      if (_dataSetDefinition != null) {
         for (ColumnDisplayDefinition colDef : _dataSetDefinition.getColumnDefinitions()) {
            String columnName = "Column";
            if (colDef != null) {
               columnName = colDef.getColumnName();
            }
            result.append(columnName);
            result.append("\t");
         }
         result.append("\n");
      }

      return result.toString();
   }

   public int currentRowIndex() {
      return _iCurrent;
   }

   public boolean isValid() {
      return _iCurrent != -1;
   }

   public boolean isAllResultsRead() {
      return _rdr.isAllResultsRead();
   }

   public boolean areAllPossibleResultsOfSQLRead() {
      return _rdr.areAllPossibleResultsOfSQLRead();
   }

   public void closeStatementAndResultSet() {
      _rdr.closeStatementAndResultSet();
   }

}
