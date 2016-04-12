package ch11.annotations;

public class ArraySerializationDataFormatException extends Exception {

  private static final long serialVersionUID = 1L;

  public ArraySerializationDataFormatException() {};

  public ArraySerializationDataFormatException(String msg) {
    super(msg);
  }

  public ArraySerializationDataFormatException(Throwable cause) {
    super(cause);
  }

  public ArraySerializationDataFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArraySerializationDataFormatException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
