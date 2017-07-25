package ex21;

import static v.ArrayUtils.box;
import static v.ArrayUtils.unbox;
import static v.ArrayUtils.mean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* p264
  2.1.12  Instrument shellsort to print the number of compares divided by the array size
  for each increment. Write a test client that tests the hypothesis that this number is a
  small constant, by sorting arrays of random  Double values, using array sizes that are
  increasing powers of 10, starting at 100.
  
  My tests show that the average number of compares for each increment divided by the array 
  size increases as the array size increases from about 1.8 for array size 10**2 to about 5 
  for array size 10**6.  The way I did it was for each trial of a given array size to count 
  the number of compares for each increment, divide it by N, return the average over all
  increment and average those averages over all trials. I did that with two versions of
  shell sort, one named shellSort using with a generic array and the other named shellSortPr
  using a primitive array. For both, the compares total for a given increment was added to a 
  list just before h was updated inside of and at bottom of the outer while loop. Then at
  the end of each sort method, the list was converted to an array and it's mean was returned.
  Also, both sorts used the increment sequence of Algorithm 2.3 and produced about the same
  results within the limits of experimental error which was relatively large due to the usual
  uncontrollable effects of JVM optimization including caching and garbage collection.
 */

public class Ex2112ShellSortCompares {

  public static int compares = 0;

  public static  <T extends Comparable<? super T>> double shellSort(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int h = 1;
    while (h < N/3) h = 3*h + 1; // 1, 4, 13, 40, 121, 364, 1093, ...
    List<Double> list = new ArrayList<>();
    while (h >= 1) { // h-sort the array.
      compares = 0;
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) {
          exch(a, j, j-h);
        }
      }
      list.add(1.*compares/N);
      h = h/3;
    }
    return mean((double[]) unbox(list.toArray(new Double[0])));
  }
  
  public static double shellSortPr(double[] a) {
 // primitive implementation of shellsort
    compares = 0;
    int n = a.length;
    if (a == null || n == 0) return -1;
    int h = 1; int increments = 0;
    while (h < n/3) h = 3*h + 1; 
    double t;
    while (h >= 1) {
      increments++;
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && compare(a[j],a[j-h]); j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
    //System.out.println("increments="+increments+" compares="+compares);
    return (1.*compares/increments)/n;
  }
  
  public static boolean compare(double a, double b) {
    compares++;
    return a < b;
  }

  public static double shellSortPrUnwrapped(double[] a) {
    // primitive implementation of shellsort with unwrapped inner for loop
    int n = a.length;
    if (a == null || n == 0) 
      throw new IllegalArgumentException(
          "array can't be null or of zero length");
    List<Double> list = new ArrayList<>();
    int h = 1; long sum = 0;
    while (h < n/3) h = 3*h + 1; 
    double t;
    while (h >= 1) {
      sum = 0;
      for (int i = h; i < n; i++) {
        int j = i;
        while(true) {
          // for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          if (j >= h) {
            sum +=1;
            // this is a compare of array elements
            if (a[j] < a[j-h]) { 
              t = a[j];
              a[j] = a[j-h];
              a[j-h] = t;
            } else {
              break;
            }
          } else {
            break;
          }
          j -= h;
        }
      }
      list.add(1.*sum/n);
      h /= 3;
    }
    return mean((double[]) unbox(list.toArray(new Double[0])));
    //    return (1.*sum)/((inc-1)*n);
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    compares++;
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }

  public static void test1(int t) {
    Random r = null;
    int p = 2; int i = 100; Double[] da = null; double sum = 0;
    System.out.println("power of 10   avg compares/(increment*N)");
    while(p < 8) {
      sum = 0;
      int j = t;
      while (j > 0) {
        r = new Random(System.currentTimeMillis());
        da = (Double[]) box(r.doubles(i).toArray());
        sum += shellSort(da);
        j--;
      }
      System.out.printf("%-3d           %5.3f\n", p, 1.*sum/t);
      i *= 10; p++;
    }
  }

  public static void test2(int t) {
    Random r = null;
    int p = 2; int i = 100; double[] da = null; double sum = 0;
    System.out.println("power of 10   avg compares/(increment*N)");
    while(p < 8) {
      sum = 0;
      int j = t;
      while (j > 0) {
        r = new Random(System.currentTimeMillis());
        da = r.doubles(i).toArray();
        sum += shellSortPrUnwrapped(da);
        j--;
      }
      System.out.printf("%-3d           %5.3f\n", p, 1.*sum/t);
      i *= 10; p++;
    }
  }
    
    public static void test3(int t) {
      Random r = null;
      int p = 2; int i = 100; double[] da = null; double sum = 0;
      System.out.println("power of 10   avg compares/(increment*N)");
      while(p < 8) {
        sum = 0;
        int j = t;
        while (j > 0) {
          r = new Random(System.currentTimeMillis());
          da = r.doubles(i).toArray();
          sum += shellSortPr(da);
          j--;
        }
        System.out.printf("%-3d           %5.3f\n", p, 1.*sum/t);
        i *= 10; p++;
      } 
    }

  public static void main(String[] args) {

    //    double[] da = {5,4,3,2,1};
    //    double x = shellSort(da);
    //    System.out.println(x);
    //    pa(da);
    //    
    //test1(3); //using generic with comparison counting done in less()
    //  power of 10   avg compares/(increment*N)
    //  2             1.816
    //  3             2.273
    //  4             2.622
    //  5             3.488
    //  6             4.824

    //test2(3); //using primitive array and inner for loop uwrapped to a while loop
    //  power of 10   avg compares/(increment*N)
    //  2             1.757
    //  3             2.376
    //  4             2.595
    //  5             3.607
    //  6             5.023

    //test3(3); //using primitive array without unwrapped inner for loop
    // this test isn't accurate since it calculates the average by dividing the total number
    // of compares by the total number of increments used for each sort execution 
    //  power of 10   avg compares/(increment*N)
    //  2             1.842
    //  3             2.324
    //  4             2.585
    //  5             3.438
    //  6             5.029
    //  7             7.151

  }
}
