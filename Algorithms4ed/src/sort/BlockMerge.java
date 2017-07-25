package sort;

import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;


public class BlockMerge {

  private BlockMerge(){}
  
  private static int interchanges = 0;
 
  public static <T extends Comparable<? super T>> void sort(T[] a) {
    // merge the blocks using sublinear space of getBlockSize(a)   
    int[][] p;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), getBlockSize(a));
    while (true) {
      p = getPartitions(a);
      interchanges = 0;
      for (int i = 0; i < p.length-1; i++) {
        mergeBlock(a, aux, p[i][0], p[i][1], p[i+1][1]);
      }
      if (interchanges == 0) break;
    }
  }

  public static <T> int getBlockSize(T[] a) {
    int n = a.length;  int size = 0; 
    int min = Integer.MAX_VALUE; int mod = 0;
    // prefer larger block up to 15 for fewer larger merges
    for (int i = 15; i > 4; i--) {
      mod = n % i;
      if (mod < min) {
        min = mod; size = i;
      }
    }
    return size;
  }
  
  public static <T extends Comparable<? super T>> int[][] getPartitions(T[] a) {
    // return an array of sorted partitions of a
    int n = a.length; int size = getBlockSize(a); 
    int b = n/size;
    if (b*size < n) b++;    
    int[][] z = new int[b][2];
    for (int i = 0; i < b-1; i++) z[i] = new int[]{i*size, (i+1)*size-1};
    z[b-1] = new int[]{(b-1)*size, a.length-1};
    // sort each partition with selection sort per requirements
    for (int i = 0; i < z.length; i++) selectionSort(a,z[i][0],z[i][1]);
    return z;
  }
 
  public static <T extends Comparable<? super T>> void mergeBlock(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // merge sorted partitions of a
    System.arraycopy(a,lo,aux,0,mid-lo+1);
    int n = a.length, i = lo, j = mid+1, bi = 0;
    for (int k = lo; k <= mid; k++) {
      if (less(a[j], aux[bi])) { 
        a[i++] = a[j++]; interchanges++; }
      else {         
        a[i++] = aux[bi++];
        if (i != bi+lo) interchanges++;
      }
      if (j == n || j == k) break;
      if (i == j) break;
    }
    
    System.arraycopy(aux,bi,a,i,aux.length-bi);
  }
 
  public static <T extends Comparable<? super T>> void selectionSort(T[] a, int lo, int hi) { 
    for (int i = lo; i <= hi; i++) { 
      int min = i;
      for (int j = i+1; j <= hi; j++) if (less(a[j], a[min])) min = j;
      exch(a, i, min);
    }
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) {
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0; 
  }
  
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    return isSorted(a, 0, a.length - 1);
  }
  
  private static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (less(a[i], a[i-1])) return false;
    return true;
  }

  @SuppressWarnings("unused")
  private static <T extends Comparable<? super T>> void show(T[] a) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public static void main(String[] args) {

    Random r;
    
    for (int i = 2; i < 1002; i++) {
      Integer[] w = rangeInteger(1, i, 1);
      r = new Random(System.currentTimeMillis());
      shuffle(w, r); 
      sort(w);
      assert isSorted(w);
    }

  }

}
