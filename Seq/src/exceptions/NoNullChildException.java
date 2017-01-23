package exceptions;

public class NoNullChildException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NoNullChildException() {};

  public NoNullChildException(String msg) {
    super(msg);
  }

  public NoNullChildException(Throwable cause) {
    super(cause);
  }

  public NoNullChildException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoNullChildException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

