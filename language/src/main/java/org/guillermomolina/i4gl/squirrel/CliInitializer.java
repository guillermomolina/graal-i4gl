package org.guillermomolina.i4gl.squirrel;

import net.sourceforge.squirrel_sql.client.Application;
import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class CliInitializer {

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

      Application application = new Application();
      Main.setApplication(application);
      application.initResourcesAndPrefs();
      application.initAppFiles();
      application.initDriverManager();
      application.initDataCache();
   }
}
