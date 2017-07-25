package ex11;

import static u.StringUtils.rep;

//  1.1.24  Give the sequence of values of p and q that are computed when Euclid’s algo-
//  rithm is used to compute the greatest common divisor of 105 and 24. Extend the code
//  given on page 4 to develop a program Euclid that takes two integers from the command
//  line and computes their greatest common divisor, printing out the two arguments for
//  each call on the recursive method. Use your program to compute the greatest common
//  divisor or 1111111 and 1234567.

public class Ex1124EuclidGcd {
  
  private static boolean done = false;
  private static int max = 0;

  public static int gcd(int p, int q) {
    // Implementation of Euclid's algorithm from p4
    if (q == 0) return p;
    int r = p % q;
    return gcd(q, r);
  }

  public static int gcdtrace(int p, int q) {
    // Implementation of Euclid's algorithm from p4
    if (!done) {
      int r = p > q ? p : q;
      max = (""+r).length();
      System.out.printf("%-"+max+"s %-"+max+"s\n", "p", "q");
      System.out.printf("%"+max+"s %"+max+"s\n", rep(max,'='), rep(max,'='));
      done = true;
    }
    System.out.printf("%"+max+"d %"+max+"d\n", p, q);
    if (q == 0) return p;
    int r = p % q;
    return gcdtrace(q, r);
  }
  
  
  public static void main(String[] args) {
    
    int x = gcdtrace(5,5);
    System.out.println();
    System.out.println(x);
    
    if (args.length >= 2 && args[0].matches("[0-9]+") && args[1].matches("[0-9]+")) {
      int p = new Integer(args[0]);
      int q = new Integer(args[1]);
      int gcd = gcdtrace(p,q);
      System.out.println("gcd="+gcd);
    }
    
    //  p       q      
    //  ======= =======
    //  1111111 1234567
    //  1234567 1111111
    //  1111111  123456
    //   123456       7
    //        7       4
    //        4       3
    //        3       1
    //        1       0
    //  gcd=1

    


  }

}
