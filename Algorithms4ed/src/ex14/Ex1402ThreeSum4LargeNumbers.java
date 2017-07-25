package ex14;

//  1.4.2  Modify  ThreeSum to work properly even when the  int values are so large that
//  adding two of them might cause overflow.

// I would add long casts in the sum in a new counting method like this:
//  public static int countLong(int[] a) { 
//    // Count triples that sum to 0.
//    int N = a.length;
//    int cnt = 0;
//    for (int i = 0; i < N; i++)
//      for (int j = i+1; j < N; j++)
//        for (int k = j+1; k < N; k++)
//          if ((long)a[i] + (long)a[j] + (long)a[k] == 0)
//            cnt++;
//    return cnt;
//  }

// double casts could be used similarly

// BigInteger conversion could be used as follows but it adds a lot of extra overhead:
//  public static int countBigInteger(int[] a) { 
//    // Count triples that sum to 0.
//    int N = a.length;
//    int cnt = 0;
//    for (int i = 0; i < N; i++)
//      for (int j = i+1; j < N; j++)
//        for (int k = j+1; k < N; k++)
//          if ((new BigInteger(""+a[i]).add(new BigInteger(""+a[j]))
//              .add(new BigInteger(""+a[k]))) == BigInteger.ZERO)
//            cnt++;
//    return cnt;
//  }

// countLong and countBigInteger are avaialable for use in analysis.ThreeSum.java

public class Ex1402ThreeSum4LargeNumbers {

  public static void main(String[] args) {

  }

}
