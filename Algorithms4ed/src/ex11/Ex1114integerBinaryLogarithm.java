package ex11;

//  1.1.14  Write a static method  lg() that takes an  int value  N as argument and returns
//  the largest  int not larger than the base-2 logarithm of  N . Do not use  Math 

// text p.185
// lg N binary logarithm :  log 2 N ( x such that 2**x =  N )
// ⎣lg N⎦ integer binary logarithm : largest integer not greater than lgN 
//    ( # bits in binary representation of N ) – 1 (should be N+1 -1 according to
// https://en.wikipedia.org/wiki/Binary_logarithm#Integer_rounding

public class Ex1114integerBinaryLogarithm {
 
  public static int integerBinaryLogarithm(int n) {
    // https://en.wikipedia.org/wiki/Binary_logarithm#Recursive_approximation
    // find int x such that 2**x ≤ n < 2**x+1
    assert n > 0; // log undefined for 0
    // case x = 0:
    if (1<=n && n<2) return 0;  
    int c = 1;
    int p = 1;
    while(true) {
      c++;
      p*=2;
      if (p <= n && n < p*2) return c;
    }
  
  }

  public static void main(String[] args) {
        
    for (int i = 1; i < 11; i++) {
      int x = (int) Math.pow(2, i);
      System.out.printf("%5d  %3d\n", x, integerBinaryLogarithm(x));
    }
//      2    2
//      4    3
//      8    4
//     16    5
//     32    6
//     64    7
//    128    8
//    256    9
//    512   10
//   1024   11

  }

}
