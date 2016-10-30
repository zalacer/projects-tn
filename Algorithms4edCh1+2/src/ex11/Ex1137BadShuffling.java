package ex11;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.StdRandom;

//  1.1.37 Bad shuffling. Suppose that you choose a random integer between  0 and  N-1
//  in our shuffling code instead of one between  i and  N-1 . Show that the resulting order is
//  not equally likely to be one of the N! possibilities. Run the test of the previous exercise
//  for this version.

// the mean stddev for each i with bad shuffling is about 57% higher than with good shuffling

public class Ex1137BadShuffling {
  
  //  from p32
  public static void shuffle(double[] a) {
    int N = a.length;
    for (int i = 0; i < N; i++)
    { // Exchange a[i] with random element in a[i..N-1]
      int r = i + StdRandom.uniform(N-i);
      double temp = a[i];
      a[i] = a[r];
      a[r] = temp;
    }
  }
  
  public static void shuffle(int[] a) {
    int N = a.length;
    for (int i = 0; i < N; i++)
    { // Exchange a[i] with random element in a[i..N-1]
      int r = i + StdRandom.uniform(N-i);
      int temp = a[i];
      a[i] = a[r];
      a[r] = temp;
    }
  }
  
  public static void badShuffle(int[] a) {
    int N = a.length;
    for (int i = 0; i < N; i++)
    { // Exchange a[i] with random element in a[i..N-1]
      int r = StdRandom.uniform(N);
      int temp = a[i];
      a[i] = a[r];
      a[r] = temp;
    }
  }
  
  public static int[][] shuffleTest(int m, int n) {
    int[][] b = new int[m][m];
    int[] a = new int[m];
    for (int k = 0; k < n; k++) {
      for (int i = 0; i < m; i++) a[i] = i;
      shuffle(a);
      for (int j = 0; j < m; j++) {
        b[a[j]][j]++;
      }
    }
    return b;
  }
  
  public static int[][] badShuffleTest(int m, int n) {
    int[][] b = new int[m][m];
    int[] a = new int[m];
    for (int k = 0; k < n; k++) {
      for (int i = 0; i < m; i++) a[i] = i;
      badShuffle(a);
      for (int j = 0; j < m; j++) {
        b[a[j]][j]++;
      }
    }
    return b;
  }


  public static void main(String[] args) {
    
    int[][] r = shuffleTest(10,1000);
    pa(r,75,1,1);
    //  [[94,115,100,105,92,93,110,102,101,88],
    //   [104,98,82,110,111,108,86,113,108,80],
    //   [111,91,105,94,99,75,120,98,93,114],
    //   [112,96,115,114,96,110,97,88,83,89],
    //   [83,92,93,93,102,108,116,93,109,111],
    //   [123,96,111,98,95,101,94,91,112,79],
    //   [106,90,96,104,99,106,80,115,96,108],
    //   [96,117,84,85,107,93,96,98,111,113],
    //   [85,114,121,91,93,107,96,87,87,119],
    //   [86,91,93,106,106,99,105,115,100,99]]
    System.out.println(mean(r)); //100
    //for (int i = 0; i < 10; i++) System.out.println(sum(r[i]));
    // all rows sum to 1000
    double[] stddev = new double[10];
    for (int i = 0; i < 10; i++) stddev[i] = stddev(r[i]);
    System.out.println(mean(stddev)); //10.257502166246423
    System.out.println();

    r = badShuffleTest(10,1000);
    pa(r,75,1,1);
    //  [[112,100,84,102,110,100,102,93,91,106],
    //   [128,80,84,93,85,109,99,112,103,107],
    //   [105,132,103,85,97,77,104,94,96,107],
    //   [110,125,133,71,88,94,78,109,100,92],
    //   [90,107,117,136,70,80,94,102,107,97],
    //   [114,95,93,111,115,92,97,80,97,106],
    //   [85,96,104,124,124,119,85,77,102,84],
    //   [100,93,106,97,102,115,112,83,109,83],
    //   [82,89,89,100,104,106,111,121,79,119],
    //   [74,83,87,81,105,108,118,129,116,99]]
    System.out.println(mean(r)); //100.0
    //for (int i = 0; i < 10; i++) System.out.println(sum(r[i]));
    //all rows sum to 1000
    stddev = new double[10];
    for (int i = 0; i < 10; i++) stddev[i] = stddev(r[i]);
    System.out.println(mean(stddev)); //16.192476598060924
  }

}
