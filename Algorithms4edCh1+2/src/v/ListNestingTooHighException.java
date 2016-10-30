package v;

public class ListNestingTooHighException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ListNestingTooHighException() {};

  public ListNestingTooHighException(String msg) {
    super(msg);
  }

  public ListNestingTooHighException(Throwable cause) {
    super(cause);
  }

  public ListNestingTooHighException(String message, Throwable cause) {
    super(message, cause);
  }

  public ListNestingTooHighException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
