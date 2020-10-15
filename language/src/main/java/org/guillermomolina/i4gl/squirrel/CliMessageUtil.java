package org.guillermomolina.i4gl.squirrel;

public class CliMessageUtil {
   public static RuntimeException wrapRuntime(Throwable th) {
      // If th is thrown it will be outputted to the command line so for now we do
      // nothing here.
      return new RuntimeException();
   }

   public static void showMessage(CliMessageType messageType, Throwable th) {
      System.out.println(messageType.name() + ": " + th.getMessage());
   }

   public static void showMessage(CliMessageType messageType, String msg) {
      if (messageType == CliMessageType.ERROR || messageType == CliMessageType.WARNING) {
         System.err.println(messageType.name() + ": " + msg);
      } else {
         System.out.println(messageType.name() + ": " + msg);
      }
   }
}
