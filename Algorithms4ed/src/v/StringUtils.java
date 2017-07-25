package v;

import java.util.Arrays;

public class StringUtils {
  
  public static final String rep(int length, char c) {
    // create a new String consisting of char c repeated length times
    char[] data = new char[length];
    Arrays.fill(data, c);
    return new String(data);
  }
  
  public static final String space(int length) {
    // create a new String consisting of a space repeated length times
    char[] data = new char[length];
    Arrays.fill(data, ' ');
    return new String(data);
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
