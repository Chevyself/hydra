package me.googas.hydra;

public class HydraMessagingException extends Exception {

  public HydraMessagingException(String message, Throwable cause) {
    super(message, cause);
  }

  public HydraMessagingException(String message) {
    super(message);
  }
}
