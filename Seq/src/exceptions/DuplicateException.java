package exceptions;

public class DuplicateException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public DuplicateException() {};
  public DuplicateException(String msg) { super(msg); }
  public DuplicateException(Throwable cause) { super(cause); }
  public DuplicateException(String message, Throwable cause) { super(message, cause); }
  public DuplicateException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

