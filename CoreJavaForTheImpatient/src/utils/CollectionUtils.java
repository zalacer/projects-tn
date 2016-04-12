package utils;

import java.util.Collection;

public class CollectionUtils {
  
  public static <T> void printCollection(Collection<T> c) {
    for (T t : c) System.out.println(t);
  }
  
  public static <T> void printfCollection(Collection<T> c) {
    Object[] ca = c.toArray(new Object[c.size()]);
    for (int i = 0; i < ca.length - 1; i++)
      System.out.print(ca[i]+", ");
    System.out.println(ca[ca.length - 1]);
  }
  
  public static void main(String[] args) {

  }


}
