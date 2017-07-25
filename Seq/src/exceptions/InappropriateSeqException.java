package exceptions;

public class InappropriateSeqException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public InappropriateSeqException() {};
  public InappropriateSeqException(String msg) { super(msg); }
  public InappropriateSeqException(Throwable cause) { super(cause); }
  public InappropriateSeqException(String message, Throwable cause) { super(message, cause); }
  public InappropriateSeqException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

