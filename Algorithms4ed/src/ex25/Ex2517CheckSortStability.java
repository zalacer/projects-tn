package ex25;

import static java.lang.System.identityHashCode;
import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import sort.Insertion;
import sort.Heap;
import sort.Quick;
import sort.Quick3way;
import sort.Merges;
import sort.Selection;
import sort.Shell;

/* p355
  2.5.17 Check stability. Extend your check() method from Exercise 2.1.16 
  to call sort() for a given array and return  true if sort() sorts the 
  array in order in a stable manner, false otherwise. Do not assume that 
  sort() is restricted to move data only with exch().
  
  Since the exercise 2.1.16 statement has "You may use Arrays.sort() and  
  assume that it is correct", the simplest approach to implementing a check 
  for stability is to sort the test array with the test sort, sort a clone 
  of the of the unsorted test array with Arrays.sort(), build arrays of the 
  identityHashCodes of the objects in each sorted array preserving order, 
  then sort and compare the identityHashCode arrays for equality using 
  Arrays.equals(). The test sort is stable for the test array only if the 
  identityHashCode arrays are equal, assuming the sort didn't alter or remove 
  any objects it sorted, that is already tested by check() implemented for 
  exercise 2.1.16. This approach is workable (1) because the array clone() 
  method for objects performs a "shallow copy", that is it doesn't clone the 
  array elements but creates new references to them (according to
  https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#clone--);
  and (2) all the Arrays.sort methods that sort object arrays are guaranteed 
  to be stable in their documentation.
  
  Another aproach is to build a map of test array's element values to a list of 
  their identityHasCodes before sorting it. After sorting, build another similar 
  map, and compare the maps for all keys in the first map with values that are 
  lists with more than one element. If any of these lists differs in order of
  its elements between the two maps for the same key, the sort is unstable. The 
  sort is stable only if there are no such differences. Comparing all the entries
  in the two maps can be also be used to verify that the sort didn't change or 
  remove any objects. This method is implemented below in check();
  
  With a number of exercises in this section answerable most easily and completely
  with maps, one would think maps would have already been covered. If there are
  questions regarding the practical use of Java maps and other subjects in Java
  Cay S. Horstmann's book "Core Java for the Impatient" is a good reference with
  sufficient but not too many exercises - the whole book has fewer exercises than
  a chapter in Algorithms 4ed by Sedgewick and Wayne. Solutions for the its 
  exercises are available at 
     https://github.com/zalacer/projects-tn/tree/master/CoreJavaForTheImpatient.

 */

public class Ex2517CheckSortStability {

  public static <T extends Comparable<? super T>> boolean check(T[] z, Consumer<T[]> sort) {
    // return true if sort sorts z, doesn't change any object in z and is stable.
    if (z == null || sort == null) throw new IllegalArgumentException("check: "
        + " all arguments must be non-null");
    if (z.length < 2) return true;
    Map<T,List<Integer>> map1 = new HashMap<>(); List<Integer> x, y;
    for (int i = 0; i < z.length; i++) {
      if (map1.containsKey(z[i])) { x = map1.get(z[i]); x.add(identityHashCode(z[i])); }
      else { x = new ArrayList<>(); x.add(identityHashCode(z[i])); }
      map1.put(z[i], x);
    }
    int n = z.length;
    sort.accept(z);
    if (z.length != n) { System.out.println("arrays have different lengths"); return false; }
    if (!isSorted(z)) { System.out.println("sorting didn't sort"); return false; };
    Map<T,List<Integer>> map2 = new HashMap<>();
    for (int i = 0; i < z.length; i++) {
      if (map2.containsKey(z[i])) { x = map2.get(z[i]); x.add(identityHashCode(z[i])); }
      else { x = new ArrayList<>(); x.add(identityHashCode(z[i])); }
      map2.put(z[i], x);
    }
    // compare the maps
    if (map1.size() != map2.size()) {
      System.out.println("maps have different sizes");
      return false;
    }
    T[] m1 = map1.keySet().toArray(ofDim(z.getClass().getComponentType(),0));
    T[] m2 = map2.keySet().toArray(ofDim(z.getClass().getComponentType(),0));
    Arrays.sort(m1); Arrays.sort(m2);
    if (!Arrays.equals(m1,m2)) {
      System.out.println("map keysets aren't equal"); 
      return false; 
    }
    Integer[] i1, i2;
    for (T k : map1.keySet()) {
      x = map1.get(k); y = map2.get(k);
      if (x.size() != y.size()) {
        System.out.println("identityHashCode lists for key "+k+" have different sizes"); 
        return false; 
      }
      i1 = x.toArray(ofDim(Integer.class,0)); i2 = y.toArray(ofDim(Integer.class,0));
      if (!Arrays.equals(i1,i2)) {
        System.out.println("identityHashCode lists for key "+k+" aren't equal"); 
        return false; 
      }
    }
    return true;
  }
    
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    for (int i = 1; i < a.length; i++)
      if (a[i].compareTo(a[i-1])<0) return false;
    return true;
  }

  public static void main(String[] args) {
    
    Integer[] ia1 = new Integer[]{700,300,500,600,400,700,300,500,600};
    System.out.println("Selection "+check(ia1, (Integer[] g) -> Selection.sort(g))+"\n"); 
    //identityHashCode lists for key 700 aren't equal
    //Selection false
                                         //
    ia1 = new Integer[]{700,300,500,600,400,700,300,500,600};
    System.out.println("Insertion "+check(ia1, (Integer[] g) -> Insertion.sort(g))+"\n"); 
    //Insertion true
    
    ia1 =  new Integer[]{700,300,500,600,400,700,300,500,600}; 
    System.out.println("Shell "+check(ia1, (Integer[] g) -> Shell.sort(g))+"\n");
    //identityHashCode lists for key 500 aren't equal
    //Shell false
    
    ia1 =  new Integer[]{700,300,500,600,400,700,300,500,600}; 
    System.out.println("Merge "+check(ia1, (Integer[] g) -> Merges.topDownAcCoSm(g,31))+"\n");
    //Merge true
    
    ia1 =  new Integer[]{700,300,500,600,400,700,300,500,600}; 
    System.out.println("Quick "+check(ia1, (Integer[] g) -> Quick.sort(g))+"\n");
    //identityHashCode lists for key 600 aren't equal
    //Quick false
    
    ia1 =  new Integer[]{700,300,500,600,400,700,300,500,600}; 
    System.out.println("Quick3way "+check(ia1, (Integer[] g) -> Quick3way.sort(g))+"\n");
    //identityHashCode lists for key 500 aren't equal
    //Quick3way false
    
    ia1 =  new Integer[]{700,300,500,600,400,700,300,500,600}; 
    System.out.println("Heap "+check(ia1, (Integer[] g) -> Heap.sort(g))+"\n");
    //identityHashCode lists for key 600 aren't equal
    //Heap false

  }

}


