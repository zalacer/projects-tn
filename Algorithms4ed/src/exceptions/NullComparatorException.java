package exceptions;

public class NullComparatorException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public NullComparatorException() {};
  public NullComparatorException(String msg) { super(msg); }
  public NullComparatorException(Throwable cause) { super(cause); }
  public NullComparatorException(String message, Throwable cause) { super(message, cause); }
  public NullComparatorException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

