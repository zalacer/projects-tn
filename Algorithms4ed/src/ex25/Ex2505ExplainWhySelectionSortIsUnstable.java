package ex25;

import static v.ArrayUtils.*;
import static java.lang.System.identityHashCode;
import sort.Selection;

/* p353
  2.5.5  Explain why selection sort is not stable.
  
  from p249 here's the code for selection sort omitting comments:
  1  public static void sort(Comparable[] a) { 
  2    int N = a.length; // array length
  3    for (int i = 0; i < N; i++) { 
  4      int min = i; // index of minimal entr.
  5      for (int j = i+1; j < N; j++)
  6        if (less(a[j], a[min])) min = j;
  7     exch(a, i, min);
      }
    }
    
  The basic reason it isn't stable is the exch() in line 7 permanently
  inverts order of a[i] with another element having the same value and 
  index between i and min. An example of this is given below.
 
 */

public class Ex2505ExplainWhySelectionSortIsUnstable {
  
  public static <T> void showIdentityHashCodes(T[] a) {
    for (T t : a) System.out.print(identityHashCode(t)+" ");  System.out.println();
  }

  public static void main(String[] args) {
    
    // example showing inversion of order of equal elements after selection sort
//    Integer[] a = {200,200,100};
//    show(a); // 200 200 100 
//    showIdentityHashCodes(a); // 366712642 1829164700 2018699554 
//    Selection.sort(a);  
//    show(a); // 100 200 200
//    showIdentityHashCodes(a); // 2018699554 1829164700 366712642 

    Integer[] a = {300,300,600,500,700};
    show(a); // 2300 300 600 500 700 
    showIdentityHashCodes(a); // 366712642 1829164700 2018699554 
    Selection.sort(a);  
    show(a); // 100 200 200
    showIdentityHashCodes(a); // 2018699554 1829164700 366712642 

  }

}
