package exceptions;

public class NullBinaryOperatorException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public NullBinaryOperatorException() {};
  public NullBinaryOperatorException(String msg) { super(msg); }
  public NullBinaryOperatorException(Throwable cause) { super(cause); }
  public NullBinaryOperatorException(String message, Throwable cause) { super(message, cause); }
  public NullBinaryOperatorException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

