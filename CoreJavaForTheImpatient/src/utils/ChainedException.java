package utils;

// for ch06.Ch0614closeAll

public class ChainedException extends Exception {
  private static final long serialVersionUID = -191791560468885113L;

  public ChainedException () {

  }

  public ChainedException (String message) {
    super (message);
  }

  public ChainedException (Throwable cause) {
    super (cause);
  }

  public ChainedException (String message, Throwable cause) {
    super (message, cause);
  }

}
