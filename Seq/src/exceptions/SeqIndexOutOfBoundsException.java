package exceptions;

public class SeqIndexOutOfBoundsException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  public SeqIndexOutOfBoundsException() {};
  public SeqIndexOutOfBoundsException(String msg) { super(msg); }
  public SeqIndexOutOfBoundsException(Throwable cause) { super(cause); }
  public SeqIndexOutOfBoundsException(String message, Throwable cause) { super(message, cause); }
  public SeqIndexOutOfBoundsException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}

