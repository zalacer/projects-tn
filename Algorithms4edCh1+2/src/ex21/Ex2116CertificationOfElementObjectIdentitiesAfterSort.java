package ex21;

import static java.lang.System.identityHashCode;

import java.util.Arrays;
import java.util.function.Consumer;

import sort.Selection;
import sort.Insertion;
import sort.Shell;

/* p265
  2.1.16 Certification. Write a  check() method that calls sort() for a given 
  array and  returns  true if  sort() puts the array in order and leaves the 
  same set of objects in the array as were there initially, false otherwise. 
  Do not assume that sort() is restricted to move data only with exch(). You 
  may use Arrays.sort() and assume that it is correct.

 */

public class Ex2116CertificationOfElementObjectIdentitiesAfterSort {
  
  public static <T extends Comparable<? super T>> boolean check(T[] z, Consumer<T[]> sort) {
    // return true if sort sorts z and does not change the objects in z.
    // assumes sort is void; if sort isn't void, another check method must be
    // implemented where sort is a Function.
    if (z == null || sort == null) throw new IllegalArgumentException("check: "
        + " all arguments must be non-null");
    if (z.length < 2) return true;
    int n = z.length;
    sort.accept(z);
    if (z.length != n) return false;
    if (!isSorted(z)) return false;
    return checkObjectIdentities(z, Arrays.copyOf(z,n));
  }

  public static <T> boolean checkObjectIdentities(T[] u, T[] v) {
    // return true if all the objects in u are identical to 
    // those in v disregarding order, else return false
    if ((u == null || u.length == 0) && (v == null || v.length == 0)) return true;
    if (u == null && v != null && v.length > 0) return false;
    if (v == null && u != null && u.length > 0) return false;
    if (u != null && v!= null && u.length != v.length) return false;
    int[] idu = new int[u.length];
    int[] idv = new int[v.length];
    for (int i = 0; i < u.length; i++) idu[i] = identityHashCode(u[i]);
    Arrays.sort(idu);
    for (int i = 0; i < v.length; i++) idv[i] = identityHashCode(v[i]);
    Arrays.sort(idv);
    return Arrays.equals(idu,idv);
  }
  
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    for (int i = 1; i < a.length; i++)
      if (a[i].compareTo(a[i-1])<0) return false;
    return true;
  }

  public static void main(String[] args) {

    Integer[] ia1 = new Integer[]{700,300,500,600,400};
    System.out.println(check(ia1, (Integer[] g) -> Selection.sort(g))); //true
    
    ia1 = new Integer[]{700,300,500,600,400};
    System.out.println(check(ia1, (Integer[] g) -> Insertion.sort(g))); //true
    
    ia1 = new Integer[]{700,300,500,600,400};
    System.out.println(check(ia1, (Integer[] g) -> Shell.sort(g))); //true
    
  }

}
