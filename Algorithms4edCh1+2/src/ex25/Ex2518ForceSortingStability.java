package ex25;

import static java.lang.System.identityHashCode;
import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import sort.Heap;
import sort.Quick;
import sort.Quick3way;
import sort.Selection;
import sort.Shell;

import v.Tuple2C;

/* p355
  2.5.18 Force stability. Write a wrapper method that makes any sort stable 
  by creating a new key type that allows you to append each key’s index to 
  the key, call sort() , then restore the original key after the sort.

 */

public class Ex2518ForceSortingStability {
  
  public static <T extends Comparable<? super T>> void stable(T[] z, String alg) {
    // stably sort z with the algorithm represented by alg by coupling each z[i]
    // with its index i in a Tuple2C<Integer,T>, putting all the tuples in a 
    // Tuple2C<Integer,T>[] by z's index order, sorting the tuple array resolving 
    // equalities between the T components by comparing index components and 
    // repopulating z from the sorted Tuple2C<Integer,T>[].
    // Tuple2C is like Tuple2 it but both its parameters (K,V) extend Comparable 
    // and it implements Comparable<K,V> in its compareTo() method as follows:
    // public int compareTo(Tuple2C<K, V> o) {
    //   int d = this._2.compareTo(o._2);
    //   return d != 0 ? d : _1.compareTo(o._1);
    // }
    Consumer<Tuple2C<Integer,T>[]> c;
    switch (alg) { // including only the unstable sort algos
      case "Selection": c = (x) ->  Selection.sort(x); break;
      case "Shell":     c = (x) ->  Shell.sort(x);     break;
      case "Quick":     c = (x) ->  Quick.sort(x);     break;
      case "Quick3way": c = (x) ->  Quick3way.sort(x); break;
      case "Heap":      c = (x) ->  Heap.sort(x);      break;
      default: throw new IllegalArgumentException("stable: alg unrecognized");
    }
    Tuple2C<Integer,T>[] t = ofDim(Tuple2C.class, z.length);
    for (int i = 0; i < z.length; i++) t[i] = new Tuple2C<Integer,T>(i, z[i]);
    c.accept(t);
    for (int i = 0; i < z.length; i++) z[i] = t[i]._2;
  }

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
    System.out.println("Selection "+check(ia1, (Integer[] g) -> stable(g,"Selection"))); 
    // Selection true
  
    ia1 = new Integer[]{700,300,500,600,400,700,300,500,600};
    System.out.println("Shell "+check(ia1, (Integer[] g) -> stable(g,"Shell")));
    // Shell true
    
    ia1 = new Integer[]{700,300,500,600,400,700,300,500,600};
    System.out.println("Quick "+check(ia1, (Integer[] g) -> stable(g,"Quick")));  
    // Quick true
    
    ia1 = new Integer[]{700,300,500,600,400,700,300,500,600};
    System.out.println("Quick3way "+check(ia1, (Integer[] g) -> stable(g,"Quick3way")));
    // Quick3way true
    
    ia1 = new Integer[]{700,300,500,600,400,700,300,500,600};
    System.out.println("Heap "+check(ia1, (Integer[] g) -> stable(g,"Heap")));
    // Heap true

  }

}


