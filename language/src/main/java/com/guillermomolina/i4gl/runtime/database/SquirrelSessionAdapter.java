package com.guillermomolina.i4gl.runtime.database;

import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.JdbcConnectionData;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectInfo;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class SquirrelSessionAdapter implements ISession {
   private static final String MUST_BE_IMPLEMENTED_MESSAGE = "Must be implemented in derived class";

   @Override
   public boolean isClosed() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public IApplication getApplication() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ISQLConnection getSQLConnection() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ISQLDatabaseMetaData getMetaData() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ISQLDriver getDriver() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ISQLAliasExt getAlias() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public SessionProperties getProperties() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void commit() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void rollback() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void close() throws SQLException {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void closeSQLConnection() throws SQLException {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setSessionInternalFrame(SessionInternalFrame sif) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void reconnect(ReconnectInfo reconnectInfo) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public Object getPluginObject(IPlugin plugin, String key) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public Object putPluginObject(IPlugin plugin, String key, Object obj) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void removePluginObject(IPlugin plugin, String key) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setMessageHandler(IMessageHandler handler) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public SessionPanel getSessionPanel() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public SessionInternalFrame getSessionInternalFrame() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public SchemaInfo getSchemaInfo() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void selectMainTab(int tabIndex) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public int getSelectedMainTabIndex() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public int addMainTab(IMainPanelTab tab) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void addToStatusBar(JComponent comp) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void removeFromStatusBar(JComponent comp) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public String getTitle() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setTitle(String newTitle) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void addToToolbar(Action action) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void addSeparatorToToolbar() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setActiveSessionWindow(ISessionWidget activeActiveSessionWindow) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ISessionWidget getActiveSessionWindow() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public boolean isfinishedLoading() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setPluginsfinishedLoading(boolean finishedLoading) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public boolean confirmClose() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setQueryTokenizer(IQueryTokenizer tokenizer) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public IQueryTokenizer getQueryTokenizer() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void setExceptionFormatter(ExceptionFormatter formatter) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public ExceptionFormatter getExceptionFormatter() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public String formatException(Throwable t) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void showMessage(Throwable th) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void showMessage(String msg) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void showErrorMessage(Throwable th) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void showErrorMessage(String msg) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void showWarningMessage(String msg) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void showWarningMessage(Throwable th) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public SQLConnection createUnmanagedConnection() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public boolean isSessionWidgetActive() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public IMainPanelTab getSelectedMainTab() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public JdbcConnectionData getJdbcData() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void addSimpleSessionListener(SimpleSessionListener simpleSessionListener) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void removeSimpleSessionListener(SimpleSessionListener simpleSessionListener) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public IIdentifier getIdentifier() {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public Object getSessionLocal(Object key) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

   @Override
   public void putSessionLocal(Object key, Object value) {
      throw new UnsupportedOperationException(MUST_BE_IMPLEMENTED_MESSAGE);
   }

}
