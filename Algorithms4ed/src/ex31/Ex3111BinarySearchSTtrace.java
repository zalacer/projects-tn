package ex31;

import static v.ArrayUtils.*;

import st.BinarySearchSTex3111;

/* p389
  3.1.11  Give a trace of the process of inserting the keys  
  E A S Y Q U E S T I O N  into an initially empty table using  
  BinarySearchST . How many compares are involved?

 */

public class Ex3111BinarySearchSTtrace {
   
  public static void main(String[] args) {

    String[] keys = "E A S Y Q U E S T I O N".split("\\s+");
    System.out.print("keys:   "); show(keys);
    int[] values = range(0, keys.length);
    System.out.print("values: "); show(values);

    System.out.println("\n                       keys[]                             vals[]");
    System.out.println("key  value  0  1  2  3  4  5  6  7  8  9   N   0  1  2  3  4  5  6  7  8  9");

    BinarySearchSTex3111<String,Integer> st = new BinarySearchSTex3111<>();
    for (int  i = 0; i < keys.length; i++) {
      st.put(keys[i], values[i]);
      System.out.println(st.trace(keys[i] ,values[i]));
    }
    
    System.out.println("\nnumber of compares = "+st.getCompares());
/*
  keys:   E A S Y Q U E S T I O N 
  values: 0 1 2 3 4 5 6 7 8 9 10 11 
  
                         keys[]                             vals[]              
  key  value  0  1  2  3  4  5  6  7  8  9   N   0  1  2  3  4  5  6  7  8  9
  E      0    E                              1   0  
  A      1    A  E                           2   1  0  
  S      2    A  E  S                        3   1  0  2  
  Y      3    A  E  S  Y                     4   1  0  2  3  
  Q      4    A  E  Q  S  Y                  5   1  0  4  2  3  
  U      5    A  E  Q  S  U  Y               6   1  0  4  2  5  3  
  E      6    A  E  Q  S  U  Y               6   1  6  4  2  5  3  
  S      7    A  E  Q  S  U  Y               6   1  6  4  7  5  3  
  T      8    A  E  Q  S  T  U  Y            7   1  6  4  7  8  5  3  
  I      9    A  E  I  Q  S  T  U  Y         8   1  6  9  4  7  8  5  3  
  O     10    A  E  I  O  Q  S  T  U  Y      9   1  6  9 10  4  7  8  5  3  
  N     11    A  E  I  N  O  Q  S  T  U  Y  10   1  6  9 11 10  4  7  8  5  3  
  
  number of compares = 12

*/    
    
  }

}
