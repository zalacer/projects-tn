package ex11;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.StdRandom;

//  1.1.36 Empirical shuffle check. Run computational experiments to check that our
//  shuffling code on page 32 works as advertised. Write a program  ShuffleTest that takes
//  command-line arguments M and N, does N shuffles of an array of size M that is initial-
//  ized with  a[i] = i before each shuffle, and prints an M-by-M table such that row  i
//  gives the number of times  i wound up in position  j for all  j . All entries in the array
//  should be close to N/M.

public class Ex1136EmpiricalShuffleCheck {
  
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


  public static void main(String[] args) {

    int[][] r = shuffleTest(10,1000);
    pa(r,75,1,1);
    //  [[99,94,116,88,104,110,94,103,98,94],
    //   [81,89,94,102,98,103,111,100,99,123],
    //   [95,102,106,102,93,91,115,93,105,98],
    //   [103,104,95,96,113,78,98,90,103,120],
    //   [94,100,88,111,96,113,103,95,117,83],
    //   [99,107,100,112,106,106,93,94,95,88],
    //   [105,100,105,89,120,89,81,109,102,100],
    //   [118,96,96,110,84,94,104,111,94,93],
    //   [105,102,109,94,99,116,101,100,83,91],
    //   [101,106,91,96,87,100,100,105,104,110]]
    System.out.println(mean(r)); //100.0

 
  }

}
