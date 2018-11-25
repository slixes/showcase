package io.slixes.core;

public class SlixesException extends Exception {

  public SlixesException() {
    super();
  }

  public SlixesException(String message) {
    super(message);
  }

  public SlixesException(Throwable cause) {
    super(cause);
  }

  public SlixesException(String message, Throwable cause) {
    super(message, cause);
  }
}
