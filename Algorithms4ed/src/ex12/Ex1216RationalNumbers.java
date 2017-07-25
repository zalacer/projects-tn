package ex12;

//  p117
//  1.2.16 Rational numbers. Implement an immutable data type  Rational for rational
//  numbers that supports addition, subtraction, multiplication, and division.
//    public class Rational
//    Rational(int numerator, int denominator)
//    Rational plus(Rational b)     sum of this number and b
//    Rational minus(Rational b)    difference of this number and b
//    Rational times(Rational b)    product of this number and b
//    Rational divides(Rational b)  quotient of this number and b
//    boolean equals(Rational that) is this number equal to  that ?
//    String toString()             string representation
//  You do not have to worry about testing for overflow (see Exercise 1.2.17), but use as
//  instance variables two  long values that represent the numerator and denominator to
//  limit the possibility of overflow. Use Euclid’s algorithm (see page 4) to ensure that the
//  numerator and denominator never have any common factors. Include a test client that
//  exercises all of your methods.

public class Ex1216RationalNumbers {

  public static class Rational {
    private final long n ;
    private final long d;

    public Rational(long n, long d) {
      long gcd = gcd(n,d);
      if (d < 0) {d = -d; n = -n; }
      this.n = n/gcd;
      this.d = d/gcd;
    }
    
    public Rational plus(Rational a) {
      return new Rational(n*a.d + d*a.n, d*a.d);      
    }
    
    public Rational minus(Rational a) {
      return new Rational(n*a.d - d*a.n, d*a.d);      
    }
    
    public Rational times(Rational a) {
      return new Rational(n*a.n, d*a.d);      
    }
    
    public Rational divides(Rational a) {
      return new Rational(n*a.d, d*a.n);      
    }
    
    public long gcd(long p, long q) {
      if (q<0) q=-q;
      if (p<0) p=-p;
      while (q > 0) {
          long t = q;
          q = p % q;
          p = t;
      }
      return p;
    }
//    //gcd(a, b) · lcm(a, b) = a · b
//    private long lcm(long p, long q) {
//      if (q<0) q=-q;
//      if (p<0) p=-p;
//      return p * (q / gcd(p, q));
//    }
    
    public boolean equals(Rational that) {
      return (n == that.n && d == that.d);
    }
    
    public String toString() {
      if (d==1) return ""+n;
      else return (""+n+"/"+d);
    }

  }


  public static void main(String[] args) {
    
    Rational x, y, z;

    // 1/2 + 1/3 = 5/6
    x = new Rational(1, 2);
    y = new Rational(1, 3);
    z = x.plus(y);
    System.out.println(z);

    // 8/9 + 1/9 = 1
    x = new Rational(8, 9);
    y = new Rational(1, 9);
    z = x.plus(y);
    System.out.println(z);

    // 1/200000000 + 1/300000000 = 1/120000000
    x = new Rational(1, 200000000);
    y = new Rational(1, 300000000);
    z = x.plus(y);
    System.out.println(z);

    // 1073741789/20 + 1073741789/30 = 1073741789/12
    x = new Rational(1073741789, 20);
    y = new Rational(1073741789, 30);
    z = x.plus(y);
    System.out.println(z);

    //  4/17 * 17/4 = 1
    x = new Rational(4, 17);
    y = new Rational(17, 4);
    z = x.times(y);
    System.out.println(z);

    // 3037141/3247033 * 3037547/3246599 = 841/961 
    x = new Rational(3037141, 3247033);
    y = new Rational(3037547, 3246599);
    z = x.times(y);
    System.out.println(z);

    // 1/6 - -4/-8 = -1/3
    x = new Rational( 1,  6);
    y = new Rational(-4, -8);
    z = x.minus(y);
    System.out.println(z);
    
    //  5/6
    //  1
    //  1/120000000
    //  1073741789/12
    //  1
    //  841/961
    //  -1/3


  }


}
