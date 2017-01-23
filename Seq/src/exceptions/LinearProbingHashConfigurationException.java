package exceptions;

public class LinearProbingHashConfigurationException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public LinearProbingHashConfigurationException() {};
  public LinearProbingHashConfigurationException(String msg) { super(msg); }
  public LinearProbingHashConfigurationException(Throwable cause) { super(cause); }
  public LinearProbingHashConfigurationException(String message, Throwable cause) { super(message, cause); }
  public LinearProbingHashConfigurationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

