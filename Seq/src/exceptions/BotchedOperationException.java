package exceptions;

public class BotchedOperationException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public BotchedOperationException() {};
  public BotchedOperationException(String msg) { super(msg); }
  public BotchedOperationException(Throwable cause) { super(cause); }
  public BotchedOperationException(String message, Throwable cause) { super(message, cause); }
  public BotchedOperationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

