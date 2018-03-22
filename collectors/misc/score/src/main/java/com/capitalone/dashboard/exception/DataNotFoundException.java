package com.capitalone.dashboard.exception;

public class DataNotFoundException extends Exception {
  public DataNotFoundException() {
    super();
  }

  public DataNotFoundException(String message) {
    super(message);
  }

  public DataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataNotFoundException(Throwable cause) {
    super(cause);
  }
}
