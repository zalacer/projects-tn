package ex11;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.io.File;
import java.io.FileNotFoundException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

import ejs.Serialization; // this is my EverydayJavaSerialization system
// it's available at:
// https://github.com/zalacer/projects-tn/tree/master/EverydayJavaSerialization

// 1.1.19  Run the following program on your computer:
//    public class Fibonacci {
//      public static long F(int N) {
//        if (N == 0) return 0,
//        if (N == 1) return 1,
//        return F(N-1) + F(N-2),
//      }
//    
//      public static void main(String[] args) {
//        for (int N = 0, N < 100, N++)
//          System.out.println(N + " " + F(N)),
//      }
//    }

// What I did was to copy the data for the first 501 fibonacci numbers from 
// http://planetmath.org/node/37680/source into a BigInt[], then serialize
// that to a file. Then it can be retrieved by deserialization and used as
// a lookup table.

// Another approach that I implemented is multiplying 2x2 long arrays of the
// form [11
//       10]
// when that's done the value of index [0][0] in the nth result is the nth
// fibonacci number.  Fibonacci numbers computed quickly this way and streamed with
// Arrays methods. See 
public class Ex1119Fibonacci {

  public static long F(int N) {
    // this takes too long to run
    if (N == 0) return 0;
    if (N == 1) return 1;
    return F(N-1) + F(N-2);
  }

  //BigInteger fibonacci number generator
  public static BigInteger f(int n) {
    if (n == 0) return ZERO;
    if (n == 1) return ONE;
    return f(n-1).add(f(n-2));
  }
  
  public static long[][] matrixMul(long[][] a, long[][]b) {
    // for square 2D arrays with same dimensions
    int N = a.length;
    long[][] c = new long[N][N];
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++) { 
        for (int k = 0; k < N; k++)
          c[i][j] += a[i][k]*b[k][j];
      }
    return c;
  }
  
  public static long fibMatrix(int n) {
    // this is iteratively recursive
    assert n > 0;
    long[][] a = new long[2][2];
    a[0] = new long[]{1,1};
    a[1] = new long[]{1,0};
    long[][] r = new long[2][2];
    r[0] = new long[]{1,0};
    r[1] = new long[]{0,1};
    for (int i = 1; i < n; i++) {
      r = matrixMul(r,a);
    }
    return r[0][0];
  }
  
  public static void fibStream(int n) {
    assert n > 0;
    long[][] a = new long[2][2];
    a[0] = new long[]{1,1};
    a[1] = new long[]{1,0};
    long[][][] oa = new long[n][2][2]; 
    Arrays.parallelSetAll(oa, i -> a); 
    Arrays.parallelPrefix(oa, (x, y) -> matrixMul(x,y));
    Arrays.stream(oa).forEach(x -> System.out.print(x[0][0]+","));
  }

  // serialization class initialization
  public static final Serialization serialization = new Serialization();

  // serialization methods
  
  public static final void setUseAnnotation(boolean b) {
    serialization.setUseAnnotation(b);
  }

  public static final String ser(Object o) {
    return serialization.ser(o);
  }

  public static final Object des (String s) {
    return serialization.des(s);
  }

  public static final Object serdes(Object o) {
    return serialization.serdes(o);
  }

  public final static void copyString2File(String s, String pathName) {
    Serialization.copyString2File(s, pathName);
  }

  public static final String copyFile2String(String pathName) {
    return Serialization.copyFile2String(pathName);
  }

  public static final void gzipString2File(String s, String pathName) {
    Serialization.gzipString2File(s, pathName);
  }

  public static final String gunzipFile2String(String pathName) {
    return Serialization.gunzipFile2String(pathName);
  }


  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(f(55));

    // Demo of using raw data to populate an array, serializing and deserializing it
    // The raw fibonacci data is in fibraw.txt
    // This code below reads it into BigInteger[] fib:
    BigInteger[] fib = new BigInteger[501];
    Scanner sc = new Scanner(new File("fibraw.txt"));
    int c = -1;
    while(sc.hasNextLine()) {
      String line = sc.nextLine();
      line = line.replaceFirst("^\\d+&", "");
      c++;
      if (c > 500) break;
      fib[c] = new BigInteger(line);
    }
    sc.close();

    // This serializes fib to the file fib501Array.ser with Base64 encoding and gzipped
    gzipString2File(ser(fib), "fib501Array.ser");
    
    // This deserializes fib501Array.ser into fib2
    BigInteger[] fib2 = (BigInteger[]) des(gunzipFile2String("fib501Array.ser"));
    assert Arrays.equals(fib, fib2);
    
    // This retrieves the 1st 501 fibonacci numbers from fib2 and prints them:
    System.out.println("Fibonacci Numbers");
    System.out.println("=================");
    for (int i = 0; i < fib2.length; i++) 
      System.out.printf("%-4d%d\n", i, fib2[i]);

    // using arrays to calculate and stream fibonacci numbers
    System.out.println(fibMatrix(55)); 
    // 139583862445
    fibStream(22); 
    //1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765,10946,17711,28657,
    
  }

}
