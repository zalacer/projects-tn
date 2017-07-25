package ex14;

import java.util.Arrays;
import java.util.Random;

import analysis.Timer;

//  p210
//  1.4.14 4-sum. Develop an algorithm for the 4-sum problem.

public class Ex1414FourSum {
  
  public static int countQuadruplesSlowest(int[] a) { 
    // count quadruples that sum to 0. duplicate elements are allowed.
    // this is the brute force solution.
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)                  //1
      for (int j = i+1; j < N; j++)              //~N
        for (int k = j+1; k < N; k++)            //~(N**2)/2 
          for (int l = k+1; l < N; l++)          //~(N**3)/6
            if (a[i] + a[j] + a[k] + a[l] == 0)  //~(N**4)/24
              cnt++;
    return cnt;
  }
  
  public static int countQuadruplesFaster(int[] a) { 
    // count quadruples that sum to 0. duplicate elements are allowed
    // running time ~(N**3)logN/3
    Arrays.sort(a); //
    int N = a.length;
    int cnt = 0;
    int t = 0;
    int lof = 0;
    int sum = 0;
    for (int i = 0; i < N; i++)
      for (int j = i+1; j < N; j++)
        for (int k = j+1; k < N; k++) {
          sum = -a[i]-a[j]-a[k];
          lof = largestIndexOf(a, sum);
          if (lof > k) {
            t = a[i] == sum ? lof-i : a[j] == sum ? lof-j : a[k] == sum ? lof-k : 0;
            cnt+=(howManyOf(a, sum) - t);
          }
        }
    return cnt;
  }
  
  public static int countTriplesSlowest(int[] a) { 
    // Count triples that sum to 0.
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)        //1
      for (int j = i+1; j < N; j++)    //~N
        for (int k = j+1; k < N; k++)  //~(N**2)/2 
          if (a[i] + a[j] + a[k] == 0) //~(N**3)/6
            cnt++;
    return cnt;
  }
  
  public static int countTriplesFaster(int[] a) { 
    // Count triples that sum to 0. duplicate elements are allowed
    // running time ~(N**2)3logN/2
    Arrays.sort(a); //NlogN
    int N = a.length;
    int cnt = 0;
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
  
  public static void main(String[] args) {
    
//  performance
    Random r = new Random(977); 
    int[] a = r.ints(1001,-100,101).toArray();
    
    Timer t = new Timer();
    int x = countQuadruplesSlowest(a);
    t.stop(); //24586 ms
    System.out.println(x); //138025974
    
    t.reset();
    int y = countQuadruplesFaster(a);
    t.stop(); //6927 ms
    System.out.println(y); //138025974

  }

}
