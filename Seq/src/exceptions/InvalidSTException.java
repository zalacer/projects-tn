package exceptions;

public class InvalidSTException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public InvalidSTException() {};
  public InvalidSTException(String msg) { super(msg); }
  public InvalidSTException(Throwable cause) { super(cause); }
  public InvalidSTException(String message, Throwable cause) { super(message, cause); }
  public InvalidSTException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

