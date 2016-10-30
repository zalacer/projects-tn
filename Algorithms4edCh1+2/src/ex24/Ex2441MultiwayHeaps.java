package ex24;

import static v.ArrayUtils.*;

import java.security.SecureRandom;



/* p335
  2.4.41 Multiway heaps. Implement a version of heapsort based on complete 
  heap ordered 3-ary and 4-ary trees, as described in the text. Count the 
  number of compares used by each and the number of compares used by the 
  standard implementation, for randomly ordered distinct keys with N=10^3, 
  10^6, and 10^9.
  
  In the text multiway heaps are discussed on p319. The standard implementation
  implements complete heap ordered 3-ary trees in array representation.
  
 */

public class Ex2441MultiwayHeaps {
  
  public static void main(String[] args) {
    
    // sort.HeapIntEx2416Ex2441.sort and sort.BinaryAndTernaryHeapSort.sort
    // are instrumented to return the total number of compares they use.
    // sort.HeapIntEx2416Ex2441.sort is the standard implementation using
    // int arrays. sort.BinaryAndTernaryHeapSort.sort also uses int arrays
    // and a boolean arg toggles it between binary and ternary heaps.
    
    SecureRandom r = new SecureRandom();
    
    int[] a = range(1,1001); // 1K
    shuffle(a,r);
    int[] b = a.clone(); int[] c = a.clone();
    
    // binary heap (3-ary tree)
    System.out.println(sort.HeapIntEx2416Ex2441.sort(a));                 // 16840
    // binary heap (3-ary tree)
    System.out.println(sort.BinaryAndTernaryHeapSort.sort(b,true));       // 18832
    // ternary heap (4-ary tree)
    System.out.println(sort.BinaryAndTernaryHeapSort.sort(c,false)+"\n"); // 16421

    a = range(1,10000001); // 1M
    shuffle(a,r);
    b = a.clone(); c = a.clone();
    
    // binary heap (3-ary tree)
    System.out.println(sort.HeapIntEx2416Ex2441.sort(a));                 // 434639005
    // binary heap (3-ary tree)
    System.out.println(sort.BinaryAndTernaryHeapSort.sort(b,true));       // 454642502
    // ternary heap (4-ary tree)
    System.out.println(sort.BinaryAndTernaryHeapSort.sort(c,false)+"\n"); // 416087763
        
    a = range(1,1000000000); 
    shuffle(a,r);                  
    b = a.clone(); c = a.clone();               
    System.out.println(sort.HeapIntEx2416Ex2441.sort(a));
    // binary heap (3-ary tree)
    System.out.println(sort.BinaryAndTernaryHeapSort.sort(b,true));
    // ternary heap (4-ary tree)
    System.out.println(sort.BinaryAndTernaryHeapSort.sort(c,false));
  
  }

}
