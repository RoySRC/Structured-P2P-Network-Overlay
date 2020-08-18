package cs455.overlay.util;

import java.awt.*;

public class LOGGER {

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";
  public String className = null;

  public boolean log_status = false;

  private synchronized void print(String color, String msgType, String msg) {
    if (log_status) {
      System.out.print(color);
      System.out.print("["+msgType+"]: ");
      System.out.print(ANSI_RESET);
      System.out.println(className+"::"+msg);
      System.out.flush();
    }
  }

  public synchronized void info(String msg) {
    print(ANSI_GREEN, "INFO", msg);
  }

  public synchronized void printStackTrace(Exception e) {
    error("StackTrace:");
    if (log_status)
      e.printStackTrace();
  }

  public synchronized void warning(String msg) {
    print(ANSI_PURPLE, "WARN", msg);
  }

  public synchronized void error(String msg) {
    print(ANSI_RED, "ERROR", msg);
  }

  public synchronized String BLACK(String msg) {
    return ANSI_BLACK+msg+ANSI_RESET;
  }

  public synchronized String RED(String msg) {
    return ANSI_RED+msg+ANSI_RESET;
  }

  public synchronized String GREEN(String msg) {
    return ANSI_GREEN+msg+ANSI_RESET;
  }

  public synchronized String YELLOW(String msg) {
    return ANSI_YELLOW+msg+ANSI_RESET;
  }

  public synchronized String BLUE(String msg) {
    return ANSI_BLUE+msg+ANSI_RESET;
  }

  public synchronized String PURPLE(String msg) {
    return ANSI_PURPLE+msg+ANSI_RESET;
  }

  public synchronized String CYAN(String msg) {
    return ANSI_CYAN+msg+ANSI_RESET;
  }

  public synchronized String WHITE(String msg) {
    return ANSI_WHITE+msg+ANSI_RESET;
  }

  public LOGGER(String className) {
    this.className = className;
  }

}
