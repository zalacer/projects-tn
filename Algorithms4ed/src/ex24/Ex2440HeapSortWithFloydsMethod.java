package ex24;

import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.security.SecureRandom;

/* p335
  2.4.40 Floyd’s method. Implement a version of heapsort based on Floyd’s 
  sink-to-the-bottom-and-then-swim idea, as described in the text. Count 
  the number of compares used by your program and the number of compares 
  used by the standard implementation, for randomly ordered distinct keys 
  with N = 10^3, 10^6, and 10^9.
  
 */

public class Ex2440HeapSortWithFloydsMethod {
  
  public static void main(String[] args) {
    
    // sort.Heap.sort and sort.HeapFloyd.sort are instrumented 
    // to return the total number of compares they use.
    
    SecureRandom r = new SecureRandom();
    
    Integer[] a = rangeInteger(1,1001); // 1K
    shuffle(a,r);
    Integer[] b = a.clone();
    
    System.out.println(sort.Heap.sort(a));           // 16810
    System.out.println(sort.HeapFloyd.sort(b)+"\n"); // 10541
    
    a = rangeInteger(1,10000001); // 1M
    shuffle(a,r);
    b = a.clone();
    
    System.out.println(sort.Heap.sort(a));           // 4.34641364E8
    System.out.println(sort.HeapFloyd.sort(b)+"\n"); // 2.38618549E8
    
    a = rangeInteger(1,1000000000); 
    shuffle(a,r);                  
    b = a.clone();                
    System.out.println(sort.Heap.sort(a));  
    System.out.println(sort.HeapFloyd.sort(b)); 
 
  }

}
