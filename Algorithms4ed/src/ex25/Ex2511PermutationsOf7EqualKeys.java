package ex25;

import static java.lang.System.identityHashCode;
import static v.ArrayUtils.*;

import java.util.HashMap;
import java.util.Map;

import exceptions.InvalidDataException;

/* p354
  2.5.11  One way to describe the result of a sorting algorithm is to 
  specify a permutation p[] of the numbers 0 to a.length-1 , such that  
  p[i] specifies where the key originally in a[i] ends up. Give the 
  permutations that describe the results of insertion sort, selection 
  sort, shellsort, mergesort, quicksort, and heapsort for an array of 
  seven equal keys. 
 */

public class Ex2511PermutationsOf7EqualKeys {
  
  public static void showPermutation(String alg) {
    Map<Integer,Integer> map = new HashMap<>();
    Integer[] a = {999,999,999,999,999,999,999};
    int[] id = identityHashCodes(a);
    if (hasDups(id)) throw new InvalidDataException("showPermutation: "
        + "the identityhash codes of the test array aren't all unique");
    for(int i = 0; i < id.length; i++) map.put(id[i],i);
    switch (alg.toLowerCase()) {
      case "insertion" : sort.Insertion.sort(a); break;
      case "selection" : sort.Selection.sort(a); break;
      case "shell" : sort.Shell.sort(a); break;
      case "merge" : sort.Merges.topDown(a); break; 
      case "quick" : sort.Quick.sort(a); break;
      case "heap"  : sort.Heap.sort(a); break;
      default: throw new IllegalArgumentException(
          "showPermutation: alg not recognized");   
    }
    int[] r = new int[a.length];
    for (int i = 0; i < a.length; i++) 
      r[map.get(identityHashCode(a[i]))] = i; 
    show(r);
  }
 
  public static void main(String[] args) {

    String[] algs = {"insertion","selection","shell","merge","quick","heap"};
    for (String s : algs) { 
      System.out.printf("%9s : ",s); showPermutation(s); 
    }
    /*
    insertion : 0 1 2 3 4 5 6 
    selection : 0 1 2 3 4 5 6 
        shell : 0 1 2 3 4 5 6 
        merge : 0 1 2 3 4 5 6 
        quick : 0 3 2 1 4 5 6 
         heap : 6 0 1 2 3 4 5 
     */
  }

}
