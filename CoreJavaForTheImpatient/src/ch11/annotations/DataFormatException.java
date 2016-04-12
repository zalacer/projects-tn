package ch11.annotations;

public class DataFormatException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DataFormatException() {};

  public DataFormatException(String msg) {
    super(msg);
  }

  public DataFormatException(Throwable cause) {
    super(cause);
  }

  public DataFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataFormatException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
