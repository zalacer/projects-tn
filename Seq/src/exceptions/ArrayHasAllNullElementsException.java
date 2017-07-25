package exceptions;

public class ArrayHasAllNullElementsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ArrayHasAllNullElementsException() {};

  public ArrayHasAllNullElementsException(String msg) {
    super(msg);
  }

  public ArrayHasAllNullElementsException(Throwable cause) {
    super(cause);
  }

  public ArrayHasAllNullElementsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArrayHasAllNullElementsException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
