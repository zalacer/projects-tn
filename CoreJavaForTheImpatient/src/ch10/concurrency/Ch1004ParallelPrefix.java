package ch10.concurrency;

import java.util.Arrays;

// 4. One parallel operation not discussed in Section 10.3.2, “Parallel Array Operations,”
// on p. 324 is the parallelPrefix method that replaces each array element with
// the accumulation of the prefix for a given associative operation. Huh? Here is an
// example. Consider the array [1, 2, 3, 4, ...] and the × operation. After
// executing Arrays.parallelPrefix(values, (x, y) -> x * y), the
// array contains
//    [1, 1 × 2, 1 × 2 × 3, 1 × 2 × 3 × 4, …]
// Perhaps surprisingly, this computation can be parallelized. First, join neighboring
// elements, as indicated here:
//    [1, 1 × 2, 3, 3 × 4, 5, 5 × 6, 7, 7 × 8]
// The gray(nonmultiplied) values are left alone. Clearly, one can make this computation in
// parallel in separate regions of the array. In the next step, update the indicated 
// elements by multiplying them with elements that are one or two positions below:
//    [1, 1 × 2, 1 × 2 × 3, 1 × 2 × 3 × 4, 5, 5 × 6, 5 × 6 × 7, 5 × 6 × 7 × 8]
// This can again be done in parallel. After log(n) steps, the process is complete. This
// is a win over the straightforward linear computation if sufficient processors are
// available.
// In this exercise, you will use the parallelPrefix method to parallelize the
// computation of Fibonacci numbers. We use the fact that the nth Fibonacci number is
// the top left coefficient of F n , where  . Make an array filled with 2 × 2
// matrices. Define a Matrix class with a multiplication method, use
// parallelSetAll to make an array of matrices, and use parallelPrefix to
// multiply them.

public class Ch1004ParallelPrefix {

  private static class Matrix {
    int a,b,c,d;
    Matrix(int a, int b, int c, int d) {
      this.a=a; this.b=b; this.c=c; this.d=d;
    }
    public Matrix m(Matrix u) {
      return new Matrix(a*u.a+b*u.c, a*u.b+b*u.d, c*u.a+d*u.c, c*u.b+d*u.d);
    }
    @Override
    public String toString() {
      return "Matrix["+a+","+b+","+c+","+d+"]";
    }
  }

  public static void main(String[] args) {

    Matrix m = new Matrix(1,2,3,4);
    System.out.println(m); // Matrix[1,2,3,4]
    Matrix m2 = new Matrix(1,0,0,1);
    System.out.println(m.m(m2)); // Matrix[1,2,3,4]

    Matrix[] m3 = new Matrix[22];
    Arrays.parallelSetAll(m3, i -> new Matrix(1,1,1,0)); 
    Arrays.parallelPrefix(m3, (x, y) -> x.m(y));
    Arrays.stream(m3).forEach(x -> System.out.print(x.a+","));
    // 1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765,10946,17711,28657

  }

}
