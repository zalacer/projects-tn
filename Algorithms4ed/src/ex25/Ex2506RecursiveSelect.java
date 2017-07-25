package ex25;

import static sort.Quick.selectRecursive;
import static v.ArrayUtils.rangeInteger;

/* p354
  2.5.6  Implement a recursive version of  select() 
  
  This is implemented in sort.Quick.selectRecursive
 
 */

public class Ex2506RecursiveSelect {
  
  public static void main(String[] args) {
    
    Integer[] a = rangeInteger(0,100);
    for (int i = 0; i < 99; i++)
      assert selectRecursive(a,i,true) == i;
 
  }

}
