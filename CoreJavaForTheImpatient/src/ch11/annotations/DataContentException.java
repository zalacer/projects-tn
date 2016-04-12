package ch11.annotations;

public class DataContentException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DataContentException() {};

  public DataContentException(String msg) {
    super(msg);
  }

  public DataContentException(Throwable cause) {
    super(cause);
  }

  public DataContentException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataContentException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
