package v;

public class NonTerminalNestingLevelWithAllNullElementsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NonTerminalNestingLevelWithAllNullElementsException() {};

  public NonTerminalNestingLevelWithAllNullElementsException(String msg) {
    super(msg);
  }

  public NonTerminalNestingLevelWithAllNullElementsException(Throwable cause) {
    super(cause);
  }

  public NonTerminalNestingLevelWithAllNullElementsException(String message, Throwable cause) {
    super(message, cause);
  }

  public NonTerminalNestingLevelWithAllNullElementsException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
