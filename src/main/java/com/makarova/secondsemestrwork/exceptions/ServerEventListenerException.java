package com.makarova.secondsemestrwork.exceptions;

public class ServerEventListenerException extends Exception {

  public ServerEventListenerException() {
  }

  public ServerEventListenerException(String message) {
    super(message);
  }

  public ServerEventListenerException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServerEventListenerException(Throwable cause) { super(cause); }
}