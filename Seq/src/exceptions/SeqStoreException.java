package exceptions;

public class SeqStoreException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public SeqStoreException() {};
  public SeqStoreException(String msg) { super(msg); }
  public SeqStoreException(Throwable cause) { super(cause); }
  public SeqStoreException(String message, Throwable cause) { super(message, cause); }
  public SeqStoreException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

