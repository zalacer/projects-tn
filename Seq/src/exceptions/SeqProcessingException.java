package exceptions;

public class SeqProcessingException extends Exception {

  private static final long serialVersionUID = 1L;
  public SeqProcessingException() {};
  public SeqProcessingException(String msg) { super(msg); }
  public SeqProcessingException(Throwable cause) { super(cause); }
  public SeqProcessingException(String message, Throwable cause) { super(message, cause); }
  public SeqProcessingException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

