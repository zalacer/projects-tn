package sort;

import static v.ArrayUtils.*;

public class NaturalMergeInt {

  public static void sort(int[] a) {
    int[] aux = new int[a.length];
    while (!mergeruns(a, aux) & !mergeruns(aux, a));
  }

  private static boolean mergeruns(int[] a, int[] aux) {
    int n = a.length;
    int i = 0, k = 0, x;
    boolean asc = true;

    while (i < n) {
      k=i;
      do x = a[i++]; while (i < n && x <= a[i]); // ascending
      while (i < n && x >= a[i]) x = a[i++];     // descending
      merge (a, aux, k, i-1, asc);
      asc=!asc;
    }
    return k == 0;
  }

  private static void merge(int[] a, int[] b, int lo, int hi, boolean asc) {
    int k = asc ? lo: hi;
    int c = asc ? 1: -1;
    int i = lo, j = hi;

    while (i <= j) {
      if (a[i] <= a[j]) b[k] = a[i++];
      else b[k] = a[j--];
      k += c;
    }
  }

  public static void main(String[] args) {
    
    

    int[] a = {9, 1, 8, 2, 7, 3, 6, 4, 5};
//    int[] c = {5, 6, 7, 8, 9, 4, 3, 2, 1};
//    int[] b = new int[a.length];
//    merge(c,b,0,a.length-1,true);
    sort(a);
    pa(a,-1);
  }

}
