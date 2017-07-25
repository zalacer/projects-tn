package ex11;

import edu.princeton.cs.algs4.StdOut;

//  1.1.6  What does the following program print?
//  int f = 0;
//  int g = 1;
//  for (int i = 0; i <= 15; i++) {
//    StdOut.println(f);
//    f = f + g;
//    g = f - g;
//  }

// 0 1 1 2 3 5 8 13 21 34 55 89 144 233 377 610

public class Ex1106Fibonacci {
  
  public static long fib(int n) {
    // http://introcs.cs.princeton.edu/java/23recursion/Fibonacci.java.html
    if (n <= 1) return n;
    else return fib(n-1) + fib(n-2);
  } 

  public static void main(String[] args) {

    int f = 0;
    int g = 1;
    for (int i = 0; i <= 15; i++) {
      StdOut.println(f);
      f = f + g;
      g = f - g;
    }
    
    System.out.println();
    int n = 15;
    for (int i = 0; i <= n; i++) StdOut.print(fib(i)+" ");
    System.out.println();

  }

}

