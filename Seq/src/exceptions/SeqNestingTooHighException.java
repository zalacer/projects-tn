package exceptions;

public class SeqNestingTooHighException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public SeqNestingTooHighException() {};
  public SeqNestingTooHighException(String msg) { super(msg); }
  public SeqNestingTooHighException(Throwable cause) { super(cause); }
  public SeqNestingTooHighException(String message, Throwable cause) { super(message, cause); }
  public SeqNestingTooHighException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

