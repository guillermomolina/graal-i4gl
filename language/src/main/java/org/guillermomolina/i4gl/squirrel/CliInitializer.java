package org.guillermomolina.i4gl.squirrel;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class CliInitializer {

   private CliInitializer() {
   }

   public static void initializeSquirrelInCliMode() {
      String squirrelHome = System.getProperty("squirrel.home");
      String squirrelUserDir = System.getProperty("squirrel.userdir");

      initializeSquirrelInCliMode(squirrelHome, squirrelUserDir);
   }

   public static void initializeSquirrelInCliMode(String squirrelHomeDir, String squirrelUserDir) {
      if (StringUtilities.isEmpty(squirrelHomeDir, true)) {
         throw new IllegalArgumentException("-Dsquirrel.home must be non null");
      }

      if (StringUtilities.isEmpty(squirrelUserDir, true)) {
         ApplicationArguments.initialize(new String[] { "-nosplash", "-no-plugins", "-home", squirrelHomeDir });
      } else {
         ApplicationArguments.initialize(
               new String[] { "-no-splash", "-no-plugins", "-home", squirrelHomeDir, "-userdir", squirrelUserDir });
      }

      initLogging();

      Application application = new Application();
      Main.setApplication(application);
      application.initResourcesAndPrefs();
      application.initAppFiles();
      application.initDriverManager();
      application.initDataCache();
   }

   private static void initLogging() {
      Properties props = new Properties();
      props.setProperty("log4j.rootLogger", "off, SquirrelAppender");
      props.setProperty("log4j.appender.SquirrelAppender",
            "net.sourceforge.squirrel_sql.client.SquirrelFileSizeRollingAppender");
      props.setProperty("log4j.appender.SquirrelAppender.layout", "org.apache.log4j.PatternLayout");
      props.setProperty("log4j.appender.SquirrelAppender.layout.ConversionPattern",
            "%d{ISO8601} [%t] %-5p %c %x - %m%n");

      PropertyConfigurator.configure(props);
   }
}