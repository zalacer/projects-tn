package exceptions;

public class NullPredicateException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public NullPredicateException() {};
  public NullPredicateException(String msg) { super(msg); }
  public NullPredicateException(Throwable cause) { super(cause); }
  public NullPredicateException(String message, Throwable cause) { super(message, cause); }
  public NullPredicateException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

