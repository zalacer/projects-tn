package ex12;

//  1.2.17 Robust implementation of rational numbers. Use assertions to develop an im-
//  plementation of  Rational (see Exercise 1.2.16) that is immune to overflow.

public class Ex1217RobustRational {

  public static class Rational {
    private final long n ;
    private final long d;

    public Rational(long n, long d) {
      assert d != 0;
      long gcd = gcd(n,d);
      if (d < 0) {d = -d; n = -n; }
      this.n = n/gcd;
      this.d = d/gcd;
    }
    
    public Rational plus(Rational a, Rational b) {
      double x = (double) n*a.d + d*a.n;
      assert x <= Long.MAX_VALUE && x >= Long.MIN_VALUE;
      double y = (double) d*a.d;
      assert y <= Long.MAX_VALUE && y >= Long.MIN_VALUE;
      return new Rational((long)x, (long)y);
    }
    
    public Rational minus(Rational a, Rational b) {
      double x = (double) n*a.d - d*a.n;
      assert x <= Long.MAX_VALUE && x >= Long.MIN_VALUE;
      double y = (double) d*a.d;
      assert y <= Long.MAX_VALUE && y >= Long.MIN_VALUE;
      return new Rational((long)x,(long)y);      
    }
    
    public Rational times(Rational a, Rational b) {
      double x = (double) n*a.n;
      assert x <= Long.MAX_VALUE && x >= Long.MIN_VALUE;
      double y = (double) d*a.d;
      assert y <= Long.MAX_VALUE && y >= Long.MIN_VALUE;
      return new Rational((long)x, (long)y);   
    }
    
    public Rational divides(Rational a, Rational b) {
      double x = (double) n*a.d;
      assert x <= Long.MAX_VALUE && x >= Long.MIN_VALUE;
      double y = (double) d*a.n;
      assert y <= Long.MAX_VALUE && y >= Long.MIN_VALUE;
      return new Rational((long)x, (long)y);      
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
    
    public boolean equals(Rational that) {
      return (n == that.n && d == that.d);
    }
    
    public String toString() {
      if (d==1) return ""+n;
      else return (""+n+"/"+d);
    }


  }


  public static void main(String[] args) {

  }


}
