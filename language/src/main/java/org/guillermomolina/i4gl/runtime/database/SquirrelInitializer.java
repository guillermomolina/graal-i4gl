package org.guillermomolina.i4gl.runtime.database;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SquirrelInitializer {
   private static boolean isInitialized = false;

   private SquirrelInitializer() {
   }

   public static void initialize() {
      String squirrelHome = System.getProperty("squirrel.home");
      String squirrelUserDir = System.getProperty("squirrel.userdir");

      initialize(squirrelHome, squirrelUserDir);
   }

   public static void initialize(String squirrelHomeDir, String squirrelUserDir) {
      if (!isInitialized) {

         if (StringUtilities.isEmpty(squirrelHomeDir, true)) {
            throw new IllegalArgumentException("-Dsquirrel.home must be non null");
         }

         if (StringUtilities.isEmpty(squirrelUserDir, true)) {
            ApplicationArguments.initialize(new String[] { "-nosplash", "-no-plugins", "-home", squirrelHomeDir });
         } else {
            ApplicationArguments.initialize(
                  new String[] { "-no-splash", "-no-plugins", "-home", squirrelHomeDir, "-userdir", squirrelUserDir });
         }

         Logger.getRootLogger().setLevel(Level.OFF);

         Application application = new Application();
         Main.setApplication(application);
         application.initResourcesAndPrefs();
         application.initAppFiles();
         application.initDriverManager();
         application.initDataCache();

         isInitialized = true;
      }
   }
}