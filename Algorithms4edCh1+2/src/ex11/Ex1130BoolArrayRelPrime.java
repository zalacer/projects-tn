package ex11;

import static v.ArrayUtils.printArray;

//  1.1.30 Array exercise. Write a code fragment that creates an N-by-N boolean array
//  a[][] such that  a[i][j] is  true if  i and  j are relatively prime (have no common
//  factors), and  false otherwise.

public class Ex1130BoolArrayRelPrime {

  public static int gcd(int a, int b) {
    // greatest common divisor O(log n)
    int t;
    while(b != 0) {
      t = a;
      a = b;
      b = t % b;
    }
    return a;
  }

  public static boolean rp(int a, int b) {
    // returns true if a and b are relatively prime else returns false
    return gcd(a, b) == 1;
  }

  public static boolean[][] rpArray(int N) {
    boolean[][] b = new boolean[N][N];
    for (int i = 0; i < N; i++) 
      for (int j = 0; j < N; j++) 
        if (rp(i,j)) b[i][j] = true;
        else b[i][j] = false; 
    return b;
  }
  
  public static void main(String[] args) {
    
    boolean[][] b = rpArray(5);
    printArray(b); // * means true
    //    0 1 2 3 4
    //  0   *      
    //  1 * * * * *
    //  2   *   *  
    //  3   * *   *
    //  4   *   *  
    
    b = rpArray(25);
    printArray(b); // * means true
    //      0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
    //   0     *                                                                     
    //   1  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
    //   2     *     *     *     *     *     *     *     *     *     *     *     *   
    //   3     *  *     *  *     *  *     *  *     *  *     *  *     *  *     *  *   
    //   4     *     *     *     *     *     *     *     *     *     *     *     *   
    //   5     *  *  *  *     *  *  *  *     *  *  *  *     *  *  *  *     *  *  *  *
    //   6     *           *     *           *     *           *     *           *   
    //   7     *  *  *  *  *  *     *  *  *  *  *  *     *  *  *  *  *  *     *  *  *
    //   8     *     *     *     *     *     *     *     *     *     *     *     *   
    //   9     *  *     *  *     *  *     *  *     *  *     *  *     *  *     *  *   
    //  10     *     *           *     *     *     *           *     *     *     *   
    //  11     *  *  *  *  *  *  *  *  *  *     *  *  *  *  *  *  *  *  *  *     *  *
    //  12     *           *     *           *     *           *     *           *   
    //  13     *  *  *  *  *  *  *  *  *  *  *  *     *  *  *  *  *  *  *  *  *  *  *
    //  14     *     *     *           *     *     *     *     *     *           *   
    //  15     *  *     *        *  *        *     *  *     *  *     *        *  *   
    //  16     *     *     *     *     *     *     *     *     *     *     *     *   
    //  17     *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *     *  *  *  *  *  *  *
    //  18     *           *     *           *     *           *     *           *   
    //  19     *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *     *  *  *  *  *
    //  20     *     *           *     *     *     *           *     *     *     *   
    //  21     *  *     *  *        *     *  *     *        *  *     *  *     *  *   
    //  22     *     *     *     *     *           *     *     *     *     *     *   
    //  23     *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *     *
    //  24     *           *     *           *     *           *     *           *   

  }

}
