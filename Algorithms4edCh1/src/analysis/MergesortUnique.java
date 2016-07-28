package analysis;

import static v.ArrayUtils.copySort;
import static v.ArrayUtils.pa;
import static v.ArrayUtils.take;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class MergesortUnique {
  private int[] vals = null;
  private int[] avals = null;

  public static int sort(int[] x) {
    // sorts unique elements of x in place returning an int specifying the 
    // number of leading indices in it for all the unique element values
    MergesortUnique m = new MergesortUnique();
    m.vals = x;
    m.avals = new int[m.vals.length/2 + 1];
    return 1 + m.mergeSort(0, m.vals.length-1);
  }
  
  private int mergeSort(int alo, int ahi) {
    // return index of last valid element
    int amid; int i1; int j1;
    if (alo < ahi) {
      amid = alo + (ahi - alo) / 2;
      i1 = mergeSort(alo, amid);
      j1 = mergeSort(amid+1, ahi);
      return merge(alo, i1, amid+1, j1);
    } else return alo;
  }

  private int merge(int i0, int i1, int j0, int j1) {
    int i; int j; int k; int lc;
    lc = i1 - i0;

    for (i = 0; i <= lc; i++)  avals[i] = vals[i+i0];
    k = i0; i = 0; j = j0;

    while (i <= lc && j <= j1) {
      if (avals[i] < vals[j]) {
        vals[k] = avals[i]; i++; k++;
      } else if (avals[i] > vals[j]) {
        vals[k] = vals[j]; j++; k++;
      } else { // skip dups
        vals[k] = avals[i]; i++; j++; k++;
      }
    }

    while (i <= lc) {
      vals[k] = avals[i]; i++; k++;
    }

    if (k <= j) {
      while (j <= j1) {
        vals[k] = vals[j]; k++; j++;
      }
    }

    return k-1;
  }
    

  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    int[] a = new int[]{5,3,3,3,4,4,4,4,1,1,1,2,2};

    int r = sort(a);
    pa(take(a, r),2000); //int[1,2,3,4,5]
    System.out.println();
    
    Random rnd = SecureRandom.getInstanceStrong();

    a = rnd.ints(20, 1, 500).toArray();
    pa(a,1000); //int[80,287,97,317,449,174,342,413,222,240,368,430,487,475,98,129,152,456,22,147]
    int[] b = copySort(a);
    pa(b,1000); //int[22,80,97,98,129,147,152,174,222,240,287,317,342,368,413,430,449,456,475,487]
    r = sort(a);
    System.out.println(r); //20
    pa(take(a, r),1000); ////int[22,80,97,98,129,147,152,174,222,240,287,317,342,368,413,430,449,456,475,487]
    System.out.println();
    
    a = rnd.ints(20, 1, 500).toArray();
    pa(a,1000); //int[40,159,236,57,167,200,458,230,50,322,265,270,360,381,40,361,329,132,249,325]
    b = copySort(a);
    pa(b,1000); //int[40,40,50,57,132,159,167,200,230,236,249,265,270,322,325,329,360,361,381,458]
    r = sort(a);
    System.out.println(r); //19
    pa(take(a, r),1000); //int[40,50,57,132,159,167,200,230,236,249,265,270,322,325,329,360,361,381,458]
    System.out.println(); 
    
    a = rnd.ints(20, 1, 8).toArray();
    pa(a,1000); //int[3,5,7,6,6,1,2,2,4,2,6,2,1,2,6,3,1,2,1,5]
    b = copySort(a);
    pa(b,1000); //int[1,1,1,1,2,2,2,2,2,2,3,3,4,5,5,6,6,6,6,7]
    r = sort(a); 
    System.out.println(r); //7
    pa(take(a, r),1000); //int[1,2,3,4,5,6,7]

  }

}
