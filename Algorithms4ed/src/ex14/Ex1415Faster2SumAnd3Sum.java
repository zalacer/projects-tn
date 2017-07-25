package ex14;

import static v.ArrayUtils.factorial;

import java.util.Arrays;
import java.util.Random;

import analysis.Timer;
import edu.princeton.cs.algs4.BinarySearch;

//  p210
//  1.4.15 Faster 3-sum. As a warmup, develop an implementation  TwoSumFaster that
//  uses a linear algorithm to count the pairs that sum to zero after the array is sorted (in-
//  stead of the binary-search-based linearithmic algorithm). Then apply a similar idea to
//  develop a quadratic algorithm for the 3-sum problem.

public class Ex1415Faster2SumAnd3Sum {
  
  public static int TwoSumFaster(int[] a) {
    // return the number of pairs in a that sum to zero in linear time 
    // excluding sorting. handles duplicated elements.
    if (a == null || a.length < 2) return 0;
    Arrays.sort(a);
    int c = 0;
    int i = 0;
    int j = a.length-1;
    if (a[i] > 0 || a[j] < 0) return 0;
    while (j > i) {
      if (a[i] > 0 || a[j] < 0) return c;
      if (-a[i] == a[j]) {
        c++; i++; j--;
      } else if (-a[i] > a[j]) {
        i--;
      } else if (-a[i] < a[j]) {
        j--;
      }
    }
    return c;
  }
  
  public static int countPairsNe2(int[] a) {
    // count pairs that sum to 0.
    // running time ~(N**2)/2
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)      //~1
      for (int j = i+1; j < N; j++)  //~N
        if (a[i] + a[j] == 0)        //~(N**2)/2
          cnt++;
    return cnt;
  }
  
  public static int countPairs2NlogN(int[] a) { 
    // count pairs that sum to 0. 
    // duplicates are allowed, of 0 in particular.
    // running time 2NlogN
    Arrays.sort(a);
    int N = a.length;
    int cnt = 0;
    int lof = 0;
    for (int i = 0; i < N; i++) {
      if (a[i] != 0 && BinarySearch.indexOf(a, -a[i]) > i) cnt++;
      else {
        lof = largestIndexOf(a, 0);
        if (lof > i) cnt+=(howManyOf(a, 0, lof) - lof + i);
      }
    }
    return cnt;
  }
  
  public static int countTriplesNe2AllUniqueElements(int[] z) {
    // return the number of triples in z that sum to zero in quadratic time 
    // excluding sorting. requires all unique elements in z.
    Arrays.sort(z);
    int a = 0;
    int b = 0;
    int c = 0;
    int start = 0;
    int end = 0;
    int n = z.length;
    int r = 0;
   
    for (int i = 0; i < n-3; i++) {
      a = z[i];
      start = i + 1;
      end = n - 1;
      while(start < end) {
        b = z[start];
        c = z[end];
        if (a+b+c == 0) {
          r++; end--;;
        } else if (a+b+c > 0) {
          end--;
        } else start++;
      }
    }
    return r;
  }
  
  public static long countTriplesNe2(int[] z) {
    // return the number of triples in z that sum to zero in quadratic time 
    // excluding sorting. handles duplicate elements.
    Arrays.sort(z);
    int a = 0;
    int b = 0;
    int c = 0;
    int start = 0;
    int end = 0;
    int n = z.length;
    long r = 0;
    long f = 0;
    int bmul = 0;
    int cmul = 0;
    int k = 0;
                    
    for (int i = 0; i < z.length-3; i++) {
      a = z[i];
      start = i + 1;
      end = n - 1;
      while(start < end) {
        b = z[start];
        c = z[end];
        if (a+b+c == 0) {
          if  (b == c) {
              f = (int) (factorial(end-start+1)/(2*factorial(end-start-1)));
              r+=f;
              break;
          } else {
            bmul = 1;
            k = start;
            while(true) {
              if (z[k+1] == b) {
                k++; bmul++;
              } else break;
            }
            cmul = 1;
            k = end;
            while(true) {
              if (z[k-1] == c) {
                k--; cmul++;
              } else break;
            }
            r+=(bmul*cmul);
            start+=bmul;
            end-=cmul;
          }
        } else if (a+b+c > 0) {
          end--;
        } else start++;
      }
    }
    return r;
  }
  
  public static long countTriplesNe3(int[] a) { 
    // Count triples that sum to 0. duplicate elements are allowed.
    // running time ~(N**3)/6
    int N = a.length;
    long cnt = 0;
    for (int i = 0; i < N; i++)           //~1
      for (int j = i+1; j < N; j++)       //~N
        for (int k = j+1; k < N; k++)     //~(N**2)/2 
          if (a[i] + a[j] + a[k] == 0) {  //~(N**3)/6
            cnt++;
//            System.out.println(a[i]+" "+a[j]+" "+a[k]);
          }
    return cnt;
  }
  
  public static long countTriplesNe2logN(int[] a) { 
    // Count triples that sum to 0. duplicate elements are allowed.
    // running time ~(N**2)3logN/2
    Arrays.sort(a); //NlogN
    int N = a.length;
    long cnt = 0;
    int t = 0;
    int lof = 0;
    int sum = 0;
    for (int i = 0; i < N; i++)                  
      for (int j = i+1; j < N; j++) {            
        t = 0;
        sum = -a[i]-a[j];
        lof = largestIndexOf(a, sum);
        if (lof > j) {  
          if (a[i] == sum) {
            t+=lof-i;
          } else if (a[j] == sum) t+=lof-j;
          cnt+=(howManyOf(a, sum)-t);
        }
    }
    return cnt;
  }
  
  public static int smallestIndexOf(int[] a, int key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int largestIndexOf(int[] a, int key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int howManyOf(int[] a, int key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = smallestIndexOf(a, key);
    if (s == -1) return 0;
    else return largestIndexOf(a, key) - s + 1;
  }
  
  public static int howManyOf(int[] a, int key, int largestIndexOfKey) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    // specialized for countPairsFaster in which largestIndexOfKey is precomputed
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = smallestIndexOf(a, key);
    if (s == -1) return 0;
    else return largestIndexOfKey - s + 1;
  }
  
  public static void main(String[] args) {
    
    // accuracy and performance:
    // the following was run straight through once.
    
    Random r = new Random(577); 
    int[] a = r.ints(10001,-500,501).toArray();
    Timer t = new Timer();
    long x = countTriplesNe3(a);
    t.stop(); //149232 ms
    System.out.println("count from countTriplesNe3: "+x+"\n");
    //count from countTriplesNe3: 124878553
    t.reset();
    long y = countTriplesNe2logN(a);
    t.stop(); //4378 ms
    System.out.println("count from countTriplesNe2logN: "+y+"\n");
    //count from countTriplesNe2logN: 124878553
    t.reset();
    long z = countTriplesNe2(a);
    t.stop(); //251 ms
    System.out.println("count from countTriplesNe2: "+z+"\n");
    //count from countTriplesNe2: 124878553
    
  }

}
