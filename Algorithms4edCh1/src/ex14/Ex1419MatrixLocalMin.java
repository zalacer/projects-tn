package ex14;

import static v.ArrayUtils.drop;
import static v.ArrayUtils.flatLine;
import static v.ArrayUtils.indexOfMin;
import static v.ArrayUtils.pa;
import static v.ArrayUtils.range;
import static v.ArrayUtils.shuffle;
import static v.ArrayUtils.take;
import static v.ArrayUtils.takeRight;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

//  p210
//  1.4.19 Local minimum of a matrix. Given an N-by-N arraya[] of N**2 distinct inte-
//  gers, design an algorithm that runs in time proportional to N to find a local minimum:
//  a pair of indices  i and  j such that  a[i][j] < a[i+1][j] , a [i][j] < a[i][j+1] ,
//  a[i][j] < a[i-1][j] , and  a[i][j] < a[i][j-1] . The running time of your program
//  should be proportional to N in the worst case.

// from http://algs4.cs.princeton.edu/14analysis/
// Hint: Find the minimum entry in row N/2, say a[N/2][j]. Check its two vertical neighbors 
// a[N/2-1][j] and a[N/2+1][j]. Recur in the half with the smaller neighbor. In that half, 
// find the minimum entry in column N/2. 

public class Ex1419MatrixLocalMin {
  
  public static int[] matrixLocalMin(int[][] z) {
    // in an int[] return 2 indices of z with corresponding elements forming
    // a matrix local minimum, if possible, else return an empty int[] which
    // may be a false negative. all elements of z must be distinct (unique).
    // edge and corner elements cannot be local minima according to its 
    // definition, which mentions nothing that implies it's ok if some 
    // neighbors are not in bounds.
    int n = z.length;
    if (z == null || n < 3) return new int[0];
    for (int i = 0; i < n; i++) 
      if (z[i].length != n) throw new IllegalArgumentException("matrixLocalMin: "
          + "all rows of z must have the length of z");
    int[] q = (int[]) flatLine(z);
    if (countPairs(q) > 0) throw new IllegalArgumentException("matrixLocalMin: "
        + "all elements of z must be unique");    
    if (z.length == 3 && z[1][1] < z[2][1] && z[1][1] < z[1][2]
        && z[1][1] < z[0][1] && z[1][1] < z[1][0]) {
      System.out.println("#array accesses="+8);
      return new int[]{1,1};
    }
    
    int a = 0; // counter for number of array accesses
    int min = 0;
    int indexOfMin = 0;
    int o = 1;
    int m = n; // n is number or rows == col length length, 
               // m is row length == number of cols
    int deltaN = 0; //adjustment for n index after scaling down
    int deltaM = 0; //adjustment for m index after scaling down
    int nindex = 0; //variable for final n index adjustment
    int mindex = 0; //variable for final m index adjustment
    
    while(true) {
      // base condition test 1 (is z large enough to have a local minimum?)
      if (n < 3 || m < 3) {
        System.out.println("#array accesses="+a);
        return new int[0];
      }
      // base condition test 2 (is z[1][1] a local minimum?)
      if (n == 3 && m == 3 && z[1][1] < z[2][1] && z[1][1] < z[1][2]
          && z[1][1] < z[0][1] && z[1][1] < z[1][0]) {
        a+=8;
        System.out.println("#array accesses="+a);
        nindex = deltaN > 0 ? deltaN+1 : 1;
        mindex = deltaM > 0 ? deltaM+1 : 1;
        return new int[]{nindex,mindex};
      }
      // base condition test 3 (is the middle element of z a local minimum?)
      // test middle indices (z[n/2][m/2])
      if (z[n/2][m/2] < z[n/2+1][m/2] && z[n/2][m/2] < z[n/2][m/2+1] 
          && z[n/2][m/2] < z[n/2-1][m/2] && z[n/2][m/2] < z[n/2][m/2-1]) {
        a+=8;
        System.out.println("#array accesses="+a);
        nindex = deltaN > 0 ? deltaN+(n/2) : n/2;
        mindex = deltaM > 0 ? deltaM+(m/2) : m/2;
        return new int[]{nindex,mindex};
      }
      // assign z to its half with the min vertical element adjacent to the min of row n/2
      if (o > 0) {
        // find indexOfMin in row n/2
        indexOfMin = indexOfMin(z[n/2]); 
        a+=m;
        // select the half with the smallest vertical element 
        // adjacent to z[n/2][indexOfMin]
        if (z[n/2-1][indexOfMin] <= z[n/2+1][indexOfMin]) {
          z = take(z, n/2);
        } else {
          z = takeRight(z, n/2);
          deltaN+=(n - n/2);
        }
        a++;
        n = n/2;
        o*=-1;
//        System.out.println("z after find indexOfMin in row "+n/2);
//        pa(z);
//        System.out.println();
        continue;
      }
      // assign z to its half with the min horizontal element adjacent to the min of col n/2  
      if (o < 0) {
        // find indexOfMin in col n/2
        min = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) if (z[i][m/2] < min) {
          min = z[i][m/2];
          indexOfMin = i;
          a+=n;
        }
        // select the half with the smallest horizontal element 
        // adjacent to z[indexOfMin][m/2]
        if (z[indexOfMin][m/2-1] <= z[indexOfMin][m/2+1]) {
          for (int i = 0; i < n; i++) z[i] = take(z[i], m/2); 
        } else {
          for (int i = 0; i < n; i++) z[i] = takeRight(z[i], m/2);
          deltaM+=(m - m/2);
        }
        a+=n*m/2;
        m = m/2;
        o*=-1;
//        System.out.println("z after find indexOfMin in col "+n/2);
//        pa(z);
//        System.out.println();
        continue;
      }
    }
  }
  
  public static int countPairs(int[] a) {
    // count pairs of identical elements in a. if an element is repeated n times, n/2
    // is added to the count, for example 1,1,1,1 and 1,1,1,1,1 both have two pairs.
    // this method has linearithmic order of growth due to Arrays.sort mergesort
    if (a == null) throw new IllegalArgumentException("countPairs: the array must be non null");
    int N = a.length;
    if (N < 2) return 0;
    Arrays.sort(a); // NlogN
    int c = 0;
    for (int i = 0; i < N-1; i++)
      if (a[i] == a[i+1]) {c++; i++;} //N
    return c;
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    int[][] a = new int[3][3];
    a[0] = new int[]{4,5,6};
    a[1] = new int[]{2,1,3};
    a[2] = new int[]{7,8,9};
    pa(matrixLocalMin(a)); //int[1,1], #array accesses=8;
    
    a = new int[6][6];
    a[0] = new int[]{10,11,12,13,14,15};
    a[1] = new int[]{2,1,3,7,8,9};
    a[2] = new int[]{16,17,18,19,20,21};
    a[3] = new int[]{22,23,24,25,26,27};
    a[4] = new int[]{28,29,30,31,32,33};
    a[5] = new int[]{34,35,36,37,38,39};
    pa(matrixLocalMin(a)); //int[1,1], #array accesses=30
    
    a[0] = new int[]{10,11,12,13,14,15};
    a[1] = new int[]{28,29,30,31,32,33};
    a[2] = new int[]{16,17,18,19,20,21};
    a[3] = new int[]{22,23,24,25,26,27};
    a[4] = new int[]{2,1,3,7,8,9};
    a[5] = new int[]{34,35,36,37,38,39};
    pa(matrixLocalMin(a)); //int[4,1], #array accesses=30
    
    a[0] = new int[]{10,11,12,13,14,15};
    a[1] = new int[]{28,29,30,31,32,33};
    a[2] = new int[]{16,17,18,19,20,21};
    a[3] = new int[]{40,41,42,2,1,43};
    a[4] = new int[]{22,23,24,25,26,27};
    a[5] = new int[]{34,35,36,37,38,39};
    pa(matrixLocalMin(a)); //int[],  #array accesses=23 
    // this result missed a[3][4] which is a local minimum
    
    a = new int[10][10];
    a[0] = new int[]{23,28,38,64,47,70,77,30,45,57};
    a[1] = new int[]{16,59,17,40,83,22,43,82,44,18};
    a[2] = new int[]{49,60,35,86,72,78,88,11,25,87};
    a[3] = new int[]{61,68,36,48,1,85,0,13,32,29};
    a[4] = new int[]{66,34,20,3,4,89,6,37,92,2};
    a[5] = new int[]{93,94,81,41,80,67,46,7,69,42};
    a[6] = new int[]{5,76,62,65,54,91,90,99,55,56};
    a[7] = new int[]{14,58,98,33,75,96,26,21,51,97};
    a[8] = new int[]{79,15,31,24,50,84,52,12,63,53};
    a[9] = new int[]{10,8,27,74,95,71,73,39,19,9};
    pa(matrixLocalMin(a)); //int[2,7], #array accesses=54
    
//#array accesses=54
    
    a = new int[10][10];
    int[] b = range(0,100);
    Random r = SecureRandom.getInstanceStrong();
//    
//    shuffle(b,r);
//    for (int i = 0; i < 10; i++) {
//      a[i] = take(b,10);
//      b = drop(b,10);
//    }
//    pa(a);
    //  int[
    //      [53,36,39,35,55,95,58,48,92,45],
    //      [85,12,64,13,30,57,71,99,34,70],
    //      [40,93,20,7,21,33,50,32,75,97],
    //      [25,42,96,80,43,84,41,8,37,18],
    //      [29,69,5,52,46,60,77,68,65,81],
    //      [19,44,89,26,3,1,76,59,78,61],
    //      [38,22,88,73,2,51,83,31,74,27],
    //      [14,4,79,9,86,47,72,10,63,28],
    //      [62,94,67,15,56,6,82,91,98,66],
    //      [11,54,90,0,87,16,17,24,49,23]] 
//    pa(matrixLocalMin(a)); //int[5,5], #array accesses=8
    
//    shuffle(b,r);
//    for (int i = 0; i < 10; i++) {
//      a[i] = take(b,10);
//      b = drop(b,10);
//    }
//    pa(a);
    //  int[
    //      [39,42,35,78,58,0,30,21,86,51],
    //      [70,83,99,80,18,4,53,73,9,12],
    //      [90,49,92,10,14,97,59,13,43,54],
    //      [61,62,45,84,24,16,57,50,95,26],
    //      [36,19,2,40,32,15,81,47,88,64],
    //      [85,55,31,89,22,66,98,5,3,56],
    //      [34,94,17,76,63,7,28,11,37,91],
    //      [79,96,27,8,60,6,38,52,48,23],
    //      [71,75,67,74,41,25,69,1,82,68],
    //      [46,87,65,33,93,77,44,29,20,72]]
//    pa(matrixLocalMin(a)); //int[7,5], #array accesses=19
//    
//    shuffle(b,r);
//    for (int i = 0; i < 10; i++) {
//      a[i] = take(b,10);
//      b = drop(b,10);
//    }
//    pa(a);
    //  int[
    //      [27,99,25,49,34,10,55,5,54,32],
    //      [73,70,24,39,86,67,12,30,98,72],
    //      [42,2,77,7,85,66,75,60,93,88],
    //      [71,53,63,56,11,26,83,90,35,0],
    //      [13,46,50,28,38,3,79,95,21,1],
    //      [62,68,94,81,52,59,19,84,31,33],
    //      [96,78,15,36,4,48,14,91,69,41],
    //      [61,74,45,65,76,37,87,6,8,43],
    //      [80,58,20,92,17,23,9,29,44,47],
    //      [97,16,64,82,89,57,51,40,18,22]]   
//    pa(matrixLocalMin(a)); //int[7,7], #array accesses=64
    
    shuffle(b,r);
    for (int i = 0; i < 10; i++) {
      a[i] = take(b,10);
      b = drop(b,10);
    }
    pa(a);
    
    pa(matrixLocalMin(a)); 
    
    
    
    
    
  }

}
